package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.annotation.common.Tag;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.common.WebId;
import com.github.learndifferent.mtm.annotation.validation.tag.TagCheck;
import com.github.learndifferent.mtm.annotation.validation.website.permission.ModifyWebsitePermissionCheck;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.PopularTagDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.entity.TagDO;
import com.github.learndifferent.mtm.entity.WebsiteDO;
import com.github.learndifferent.mtm.manager.DeleteTagManager;
import com.github.learndifferent.mtm.mapper.TagMapper;
import com.github.learndifferent.mtm.mapper.WebsiteMapper;
import com.github.learndifferent.mtm.service.TagService;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
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
 * Tag Service
 *
 * @author zhou
 * @date 2022/3/31
 */
@Service
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final WebsiteMapper websiteMapper;
    private final DeleteTagManager deleteTagManager;

    @Autowired
    public TagServiceImpl(TagMapper tagMapper,
                          WebsiteMapper websiteMapper,
                          DeleteTagManager deleteTagManager) {
        this.tagMapper = tagMapper;
        this.websiteMapper = websiteMapper;
        this.deleteTagManager = deleteTagManager;
    }

    @Override
    @ModifyWebsitePermissionCheck
    @TagCheck
    @CachePut(value = "tag:a", key = "#webId", unless = "''.equals(#result)")
    public String applyTag(@Username String username, @WebId Integer webId, @Tag String tagName) {
        String tag = tagName.trim();
        TagDO tagDO = TagDO.builder().tag(tag).webId(webId).build();
        boolean success = tagMapper.addTag(tagDO);
        return success ? tag : "";
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
    @Cacheable(value = "tag:a", key = "#webId")
    public String getTagOrReturnEmpty(Integer webId) {
        if (webId == null) {
            return "";
        }
        List<String> tags = tagMapper.getTagsByWebId(webId, 0, 1);
        return CollectionUtils.isEmpty(tags) ? "" : getFirstFromListOrReturnEmpty(tags);
    }

    private String getFirstFromListOrReturnEmpty(List<String> collection) {
        String first = collection.get(0);
        return Optional.ofNullable(first).orElse("");
    }

    @Override
    public List<WebsiteDTO> getBookmarksByUsernameAndTag(String username, String tagName, PageInfoDTO pageInfo) {

        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();

        List<Integer> ids = tagMapper.getWebIdByTagName(tagName, from, size);
        throwExceptionIfEmpty(ids);

        return getBookmarks(username, ids);
    }

    private List<WebsiteDTO> getBookmarks(String username, List<Integer> ids) {
        List<WebsiteDTO> result = new ArrayList<>();
        ids.forEach(i -> updateBookmarks(result, username, i));
        return result;
    }

    private void updateBookmarks(List<WebsiteDTO> bookmarks, String username, int webId) {
        WebsiteDO web = websiteMapper.getWebsiteDataById(webId);
        if (web == null) {
            return;
        }

        boolean isPublic = web.getIsPublic();
        String owner = web.getUserName();
        if (isPublic || owner.equalsIgnoreCase(username)) {
            WebsiteDTO w = DozerUtils.convert(web, WebsiteDTO.class);
            bookmarks.add(w);
        }
    }

    @Override
    @ModifyWebsitePermissionCheck
    public boolean deleteTag(@Username String username, @WebId Integer webId, String tagName) {
        // Web Id will not be null after checking by @ModifyWebsitePermissionCheck.
        // This will delete the tag (prefix of the key is "tag:a") of the bookmarked site
        // stored in the cache if no exception is thrown.
        return deleteTagManager.deleteTag(tagName, webId);
    }

    @Override
    @Cacheable(value = "tag:popular")
    public List<PopularTagDTO> getPopularTags(PageInfoDTO pageInfo) {
        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();

        List<PopularTagDTO> tags = tagMapper.getPopularTags(from, size);
        throwExceptionIfEmpty(tags);

        return tags;
    }

    private void throwExceptionIfEmpty(Collection<?> collection) {
        boolean isEmpty = CollectionUtils.isEmpty(collection);
        ThrowExceptionUtils.throwIfTrue(isEmpty, ResultCode.NO_RESULTS_FOUND);
    }
}
