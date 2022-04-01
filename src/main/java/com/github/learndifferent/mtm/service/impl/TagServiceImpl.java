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
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.mapper.TagMapper;
import com.github.learndifferent.mtm.mapper.WebsiteMapper;
import com.github.learndifferent.mtm.service.TagService;
import com.github.learndifferent.mtm.utils.CompareStringUtil;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public TagServiceImpl(TagMapper tagMapper, WebsiteMapper websiteMapper) {
        this.tagMapper = tagMapper;
        this.websiteMapper = websiteMapper;
    }

    @Override
    @ModifyWebsitePermissionCheck
    @TagCheck
    public boolean applyTag(@Username String username, @WebId Integer webId, @Tag String tag) {
        TagDO tagDO = TagDO.builder().tag(tag.trim()).webId(webId).build();
        return tagMapper.addTag(tagDO);
    }

    @Override
    public List<String> getTags(String username, Integer webId, PageInfoDTO pageInfo) {

        if (webId != null) {
            verifyWebPermission(username, webId);
        }

        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();

        List<String> tags = tagMapper.getTagsByWebId(webId, from, size);
        boolean isEmpty = CollectionUtils.isEmpty(tags);
        ThrowExceptionUtils.throwIfTrue(isEmpty, ResultCode.NO_RESULTS_FOUND);

        return tags;
    }

    private void verifyWebPermission(String username, Integer webId) {
        WebsiteDO web = websiteMapper.getWebsiteDataById(webId);
        ThrowExceptionUtils.throwIfNull(web, ResultCode.PERMISSION_DENIED);

        boolean isPublic = web.getIsPublic();
        if (isPublic) {
            return;
        }

        String owner = web.getUserName();
        boolean notOwner = CompareStringUtil.notEqualsIgnoreCase(username, owner);
        ThrowExceptionUtils.throwIfTrue(notOwner, ResultCode.PERMISSION_DENIED);
    }

    @Override
    public String getFirstTagOrReturnEmpty(String username, Integer webId) {
        if (cantNotGetFirstTag(username, webId)) {
            return "";
        }
        List<String> tags = tagMapper.getTagsByWebId(webId, 0, 1);
        return CollectionUtils.isEmpty(tags) ? "" : getFirstTagOrReturnEmpty(tags);
    }

    /**
     * If the user can't get the first tag, return true
     *
     * @param username username
     * @param webId    ID
     * @return If the user can't get the first tag, the method returns
     * true. Otherwise, it returns false.
     */
    private boolean cantNotGetFirstTag(String username, Integer webId) {
        if (webId == null) {
            return true;
        }
        try {
            verifyWebPermission(username, webId);
        } catch (ServiceException e) {
            // return true if doesn't have permission
            return true;
        }
        return false;
    }

    private String getFirstTagOrReturnEmpty(List<String> tags) {
        String firstTag = tags.get(0);
        return Optional.ofNullable(firstTag).orElse("");
    }

    @Override
    public List<WebsiteDTO> getBookmarksByUsernameAndTag(String username, String tagName, PageInfoDTO pageInfo) {

        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();

        List<Integer> ids = tagMapper.getWebIdByTagName(tagName, from, size);
        throwExceptionIfEmpty(ids);

        return getBookmarks(username, ids);
    }

    private void throwExceptionIfEmpty(Collection<?> collection) {
        boolean isEmpty = CollectionUtils.isEmpty(collection);
        ThrowExceptionUtils.throwIfTrue(isEmpty, ResultCode.NO_RESULTS_FOUND);
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
        return tagMapper.deleteTag(tagName, webId);
    }

    @Override
    public List<PopularTagDTO> getPopularTags(PageInfoDTO pageInfo) {
        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();

        List<PopularTagDTO> tags = tagMapper.getPopularTags(from, size);
        throwExceptionIfEmpty(tags);

        return tags;
    }
}
