package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.mapper.CommentMapper;
import com.github.learndifferent.mtm.mapper.TagMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Get additional information manager
 *
 * @author zhou
 * @date 2021/9/30
 */
@Component
public class MoreInfoManager {

    private final CommentMapper commentMapper;
    private final TagMapper tagMapper;

    @Autowired
    public MoreInfoManager(CommentMapper commentMapper, TagMapper tagMapper) {
        this.commentMapper = commentMapper;
        this.tagMapper = tagMapper;
    }

    /**
     * Get the number of comments (exclude replies) of a bookmarked website
     *
     * @param webId ID of the bookmarked website data
     * @return umber of comments of the bookmarked website
     */
    public int countCommentByWebId(int webId) {
        return commentMapper.countCommentByWebId(webId);
    }

    /**
     * Get a tag of a bookmarked site
     * <p>
     * The result will be stored in the cache
     * as the tag (prefix of the key is "tag:a") of the bookmarked site
     * </p>
     *
     * @param webId ID of the bookmarked website data
     * @return the tag, or return empty string if there is no tag
     */
    @Cacheable(value = "tag:a", key = "#webId")
    public String getTagOrReturnEmpty(int webId) {
        List<String> tags = tagMapper.getTagsByWebId(webId, 0, 1);
        return CollectionUtils.isEmpty(tags) ? "" : getFirstFromListOrReturnEmpty(tags);
    }

    private String getFirstFromListOrReturnEmpty(List<String> collection) {
        String first = collection.get(0);
        return Optional.ofNullable(first).orElse("");
    }
}