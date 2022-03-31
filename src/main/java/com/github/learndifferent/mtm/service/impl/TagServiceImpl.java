package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.annotation.common.Tag;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.common.WebId;
import com.github.learndifferent.mtm.annotation.validation.tag.TagCheck;
import com.github.learndifferent.mtm.annotation.validation.website.permission.ModifyWebsitePermissionCheck;
import com.github.learndifferent.mtm.entity.TagDO;
import com.github.learndifferent.mtm.mapper.TagMapper;
import com.github.learndifferent.mtm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Tag Service
 *
 * @author zhou
 * @date 2022/3/31
 */
@Service
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;

    @Autowired
    public TagServiceImpl(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    @Override
    @ModifyWebsitePermissionCheck
    @TagCheck
    public boolean applyTag(@Username String username, @WebId Integer webId, @Tag String tag) {
        TagDO tagDO = TagDO.builder().tag(tag).webId(webId).build();
        return tagMapper.addTag(tagDO);
    }
}
