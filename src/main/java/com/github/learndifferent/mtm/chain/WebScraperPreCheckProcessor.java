package com.github.learndifferent.mtm.chain;

import com.github.learndifferent.mtm.constant.consist.WebScraperProcessorConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.BasicWebDataDTO;
import com.github.learndifferent.mtm.manager.UserManager;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
import com.github.learndifferent.mtm.utils.CleanUrlUtil;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 1. Clean up the URL
 * 2. If the web page has already been bookmarked by the user, throw an exception.
 * 3. Verify if the web page data for that URL exists in the database.
 * 3.1. If the data exists, use the data from the database;
 * 3.2. otherwise, continue with the next processes.
 *
 * @author zhou
 * @date 2023/8/15
 */
@Component
@RequiredArgsConstructor
@Order(WebScraperProcessorConstant.PRE_CHECK_ORDER)
public class WebScraperPreCheckProcessor extends AbstractWebScraperProcessor {

    private final BookmarkMapper bookmarkMapper;
    private final UserManager userManager;

    @Override
    public BasicWebDataDTO process(@NotNull WebScraperRequest request) {
        String originUrl = request.getRequestedUrl();
        Long userId = request.getUserId();

        // clean up URL
        boolean isNoUrl = StringUtils.isBlank(originUrl);
        ThrowExceptionUtils.throwIfTrue(isNoUrl, ResultCode.URL_MALFORMED);

        String url = CleanUrlUtil.cleanup(originUrl);
        // update the requested URL
        request.setRequestedUrl(url);

        // first check if the web page is already present in the user's bookmarks.
        // if the user has already bookmarked the web page, throw an exception.
        userManager.checkIfUserBookmarked(userId, url);

        // if user didn't bookmark it
        // retrieve the bookmark data associated with the provided URL
        BasicWebDataDTO bookmarkData = bookmarkMapper.getBookmarkDataByUrl(url);

        boolean hasData = Objects.nonNull(bookmarkData);

        // return the existing web page data if it exists
        return hasData ? bookmarkData
                // if the data does not exist in the database, proceed further
                : this.next.process(request);
    }
}
