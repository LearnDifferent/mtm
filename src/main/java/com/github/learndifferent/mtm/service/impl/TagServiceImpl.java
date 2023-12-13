package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.BookmarkId;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.DataAccessType;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.Tag;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.UserId;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.PopularTagDTO;
import com.github.learndifferent.mtm.entity.TagAndCountDO;
import com.github.learndifferent.mtm.entity.TagDO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.manager.DeleteTagManager;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
import com.github.learndifferent.mtm.mapper.TagMapper;
import com.github.learndifferent.mtm.service.TagService;
import com.github.learndifferent.mtm.utils.BeanUtils;
import com.github.learndifferent.mtm.utils.PaginationUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.learndifferent.mtm.vo.BookmarkVO;
import com.github.learndifferent.mtm.vo.SearchByTagResultVO;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * Tag Service Implementation
 *
 * @author zhou
 * @date 2022/3/31
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final DeleteTagManager deleteTagManager;
    private final BookmarkMapper bookmarkMapper;

    @Override
    @AccessPermissionCheck(dataAccessType = DataAccessType.TAG_CREATE)
    @CachePut(value = "tag:a", key = "#bookmarkId", unless = "''.equals(#result)")
    public String applyTag(@UserId long userId, @BookmarkId long bookmarkId, @Tag String tagName) {
        String tag = tagName.trim();
        TagDO tagDO = TagDO.builder().tag(tag).bookmarkId(bookmarkId).build();
        try {
            // a unique index is defined on tag and bookmark_id,
            // a DuplicateKeyException will be thrown if there is a duplication
            tagMapper.addTag(tagDO);
        } catch (DuplicateKeyException e) {
            throw new ServiceException(ResultCode.TAG_EXISTS);
        }
        return tagDO.getTag();
    }

    @Override
    @Cacheable(value = "tag:all", condition = "#bookmarkId == null")
    public List<String> getTags(Integer bookmarkId, PageInfoDTO pageInfo) {

        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();

        List<String> tags = Optional.ofNullable(bookmarkId)
                .map(id -> tagMapper.getTagsByBookmarkId(id, from, size))
                .orElseGet(() -> tagMapper.getAllTags(from, size));

        throwExceptionIfEmpty(tags);
        return tags;
    }

    @Override
    @Cacheable(value = "tag:a", key = "#bookmarkId")
    public String getTagOrReturnEmpty(Integer bookmarkId) {

        return Optional.ofNullable(bookmarkId)
                // query the tags
                .map(id -> tagMapper.getTagsByBookmarkId(id, 0, 1))
                // filter the query result, make sure the result is not empty
                .filter(CollectionUtils::isNotEmpty)
                // get the first tag in collections
                .map(tags -> tags.get(0))
                // if bookmarkId is null or the SQL query result is empty,
                // return empty string
                .orElse("");
    }

    @Override
    public List<BookmarkVO> getBookmarksByUsernameAndTag(String username, String tagName, PageInfoDTO pageInfo) {

        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();
        return getBookmarksByUsernameAndTag(username, tagName, from, size);
    }

    private List<BookmarkVO> getBookmarksByUsernameAndTag(String username, String tagName, int from, int size) {
        List<Long> ids = tagMapper.getBookmarkIdsByTagName(tagName, from, size);
        throwExceptionIfEmpty(ids);

        return ids.stream()
                // get a bookmark by ID
                .map(bookmarkMapper::getBookmarkWithUsernameById)
                // bookmark should not be null
                .filter(Objects::nonNull)
                // bookmark should be public or own by the user
                .filter(b -> b.getIsPublic() || StringUtils.equalsIgnoreCase(b.getUserName(), username))
                // collect all bookmarks
                .collect(Collectors.toList());
    }

    @Override
    public SearchByTagResultVO getSearchResultsByUsernameAndTag(String username, String tagName, PageInfoDTO pageInfo) {
        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();

        List<BookmarkVO> bookmarks = getBookmarksByUsernameAndTag(username, tagName, from, size);

        int totalCount = countTags(tagName);
        int totalPages = PaginationUtils.getTotalPages(totalCount, size);

        return SearchByTagResultVO.builder()
                .bookmarks(bookmarks)
                .totalPages(totalPages)
                .build();
    }

    /**
     * Given a tag name, return the number of times that tag appears in the database
     *
     * @param tagName The name of the tag to count
     * @return The number of times the tag appears in database
     */
    private int countTags(String tagName) {
        return tagMapper.countTags(tagName);
    }

    @Override
    @AccessPermissionCheck(dataAccessType = DataAccessType.TAG_DELETE)
    public boolean deleteTag(@UserId long userId, @BookmarkId long bookmarkId, String tagName) {
        log.info("Delete tag: {}, User ID: {}, Bookmark ID: {}", tagName, userId, bookmarkId);
        // This will delete the tag (prefix of the key is "tag:a") of the bookmark
        // stored in the cache if no exception is thrown.
        return deleteTagManager.deleteTag(tagName, bookmarkId);
    }

    @Override
    @Cacheable(value = "tag:popular")
    public List<PopularTagDTO> getPopularTags(long userId, PageInfoDTO pageInfo) {
        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();

        List<TagAndCountDO> tags = tagMapper.getPopularTags(userId, from, size);
        throwExceptionIfEmpty(tags);

        return BeanUtils.convertList(tags, PopularTagDTO.class);
    }

    private void throwExceptionIfEmpty(Collection<?> collection) {
        boolean isEmpty = CollectionUtils.isEmpty(collection);
        ThrowExceptionUtils.throwIfTrue(isEmpty, ResultCode.NO_RESULTS_FOUND);
    }
}