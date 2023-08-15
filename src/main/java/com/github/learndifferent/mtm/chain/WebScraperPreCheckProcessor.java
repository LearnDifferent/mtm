package com.github.learndifferent.mtm.chain;

import com.github.learndifferent.mtm.constant.consist.WebScraperProcessorConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.BasicWebDataDTO;
import com.github.learndifferent.mtm.entity.BookmarkDO;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
import com.github.learndifferent.mtm.utils.CleanUrlUtil;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Clean up the URL and then verify if the web page data for that URL exists in the database.
 * If the data exists, check if the web page is already bookmarked by the user.
 * If the web page has already been bookmarked, throw an exception.
 * Otherwise, use the data from the database; otherwise, continue with the next processes.
 *
 * @author zhou
 * @date 2023/8/15
 */
@Component
@RequiredArgsConstructor
@Order(WebScraperProcessorConstant.PRE_CHECK_ORDER)
public class WebScraperPreCheckProcessor extends AbstractWebScraperProcessor {

    private final BookmarkMapper bookmarkMapper;

    @Override
    public BasicWebDataDTO process(@NotNull WebScraperRequest request) {
        String originUrl = request.getRequestedUrl();
        String username = request.getUsername();

        // clean up URL
        boolean isNoUrl = StringUtils.isBlank(originUrl);
        ThrowExceptionUtils.throwIfTrue(isNoUrl, ResultCode.URL_MALFORMED);

        String url = CleanUrlUtil.cleanup(originUrl);
        // update the requested URL
        request.setRequestedUrl(url);

        // Get all bookmarks that have the given URL
        List<BookmarkDO> bookmarks = bookmarkMapper.getBookmarksByUrl(url);

        // If the data does not exist in the database, proceed further
        boolean hasNoData = CollectionUtils.isEmpty(bookmarks);
        if (hasNoData) {
            return this.next.process(request);
        }

        // If the data exists,
        // first check if the web page is already present in the user's bookmarks.
        boolean hasUserBookmarked = bookmarks.stream()
                .anyMatch(bookmark -> StringUtils.equals(bookmark.getUserName(), username));
        // If the user has already bookmarked the web page, throw an exception.
        ThrowExceptionUtils.throwIfTrue(hasUserBookmarked, ResultCode.ALREADY_SAVED);

        // Finally, if the web page has not been bookmarked, return the existing web page data.
        return DozerUtils.convert(bookmarks.get(0), BasicWebDataDTO.class);
    }
}
