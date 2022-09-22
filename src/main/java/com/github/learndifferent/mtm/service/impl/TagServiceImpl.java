package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.annotation.common.Tag;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.common.WebId;
import com.github.learndifferent.mtm.annotation.validation.tag.TagCheck;
import com.github.learndifferent.mtm.annotation.validation.website.permission.ModifyWebsitePermissionCheck;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.PopularTagDTO;
import com.github.learndifferent.mtm.entity.BookmarkDO;
import com.github.learndifferent.mtm.entity.TagAndCountDO;
import com.github.learndifferent.mtm.entity.TagDO;
import com.github.learndifferent.mtm.manager.DeleteTagManager;
import com.github.learndifferent.mtm.mapper.TagMapper;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
import com.github.learndifferent.mtm.service.TagService;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.utils.PaginationUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.learndifferent.mtm.vo.BookmarkVO;
import com.github.learndifferent.mtm.vo.SearchByTagResultVO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Tag Service Implementation
 *
 * @author zhou
 * @date 2022/3/31
 */
@Service
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final BookmarkMapper bookmarkMapper;
    private final DeleteTagManager deleteTagManager;

    @Autowired
    public TagServiceImpl(TagMapper tagMapper,
                          BookmarkMapper bookmarkMapper,
                          DeleteTagManager deleteTagManager) {
        this.tagMapper = tagMapper;
        this.bookmarkMapper = bookmarkMapper;
        this.deleteTagManager = deleteTagManager;
    }

    @Override
    @ModifyWebsitePermissionCheck
    @TagCheck
    @CachePut(value = "tag:a", key = "#bookmarkId", unless = "''.equals(#result)")
    public String applyTag(@Username String username, @WebId Integer bookmarkId, @Tag String tagName) {
        String tag = tagName.trim();
        TagDO tagDO = TagDO.builder().tag(tag).webId(bookmarkId).build();
        tagMapper.addTag(tagDO);
        return tagDO.getTag();
    }

    @Override
    @Cacheable(value = "tag:all", condition = "#webId == null")
    public List<String> getTags(Integer webId, PageInfoDTO pageInfo) {

        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();

        List<String> tags = tagMapper.getTagsByWebId(webId, from, size);
        throwExceptionIfEmpty(tags);

        return tags;
    }

    @Override
    @Cacheable(value = "tag:a", key = "#bookmarkId")
    public String getTagOrReturnEmpty(Integer bookmarkId) {
        if (bookmarkId == null) {
            return "";
        }
        List<String> tags = tagMapper.getTagsByWebId(bookmarkId, 0, 1);
        return CollectionUtils.isEmpty(tags) ? "" : getFirstFromListOrReturnEmpty(tags);
    }

    private String getFirstFromListOrReturnEmpty(List<String> collection) {
        String first = collection.get(0);
        return Optional.ofNullable(first).orElse("");
    }

    @Override
    public List<BookmarkVO> getBookmarksByUsernameAndTag(String username, String tagName, PageInfoDTO pageInfo) {

        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();
        return getBookmarksByUsernameAndTag(username, tagName, from, size);
    }

    private List<BookmarkVO> getBookmarksByUsernameAndTag(String username, String tagName, int from, int size) {
        List<Integer> ids = tagMapper.getWebIdsByTagName(tagName, from, size);
        throwExceptionIfEmpty(ids);

        return getBookmarks(username, ids);
    }

    @Override
    public SearchByTagResultVO getSearchResultsByUsernameAndTag(String username, String tagName, PageInfoDTO pageInfo) {
        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();

        List<BookmarkVO> bookmarks = getBookmarksByUsernameAndTag(username, tagName, from, size);

        int totalCount = countTags(tagName);
        int totalPages = PaginationUtils.getTotalPages(totalCount, size);

        return SearchByTagResultVO.builder().bookmarks(bookmarks).totalPages(totalPages).build();
    }

    private List<BookmarkVO> getBookmarks(String username, List<Integer> ids) {
        List<BookmarkVO> result = new ArrayList<>();
        ids.forEach(i -> updateBookmarks(result, username, i));
        return result;
    }

    private void updateBookmarks(List<BookmarkVO> bookmarks, String username, int bookmarkId) {
        BookmarkDO b = bookmarkMapper.getBookmarkById(bookmarkId);
        if (b == null) {
            return;
        }

        boolean isPublic = b.getIsPublic();
        String owner = b.getUserName();
        if (isPublic || owner.equalsIgnoreCase(username)) {
            BookmarkVO bookmark = DozerUtils.convert(b, BookmarkVO.class);
            bookmarks.add(bookmark);
        }
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
    @ModifyWebsitePermissionCheck
    public boolean deleteTag(@Username String username, @WebId Integer bookmarkId, String tagName) {
        // Web Id will not be null after checking by @ModifyWebsitePermissionCheck.
        // This will delete the tag (prefix of the key is "tag:a") of the bookmarked site
        // stored in the cache if no exception is thrown.
        return deleteTagManager.deleteTag(tagName, bookmarkId);
    }

    @Override
    @Cacheable(value = "tag:popular")
    public List<PopularTagDTO> getPopularTags(PageInfoDTO pageInfo) {
        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();

        List<TagAndCountDO> tags = tagMapper.getTagAndCount(from, size, true);
        throwExceptionIfEmpty(tags);

        return DozerUtils.convertList(tags, PopularTagDTO.class);
    }

    private void throwExceptionIfEmpty(Collection<?> collection) {
        boolean isEmpty = CollectionUtils.isEmpty(collection);
        ThrowExceptionUtils.throwIfTrue(isEmpty, ResultCode.NO_RESULTS_FOUND);
    }
}