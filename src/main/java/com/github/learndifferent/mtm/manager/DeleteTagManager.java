package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.mapper.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

/**
 * Delete Tags
 *
 * @author zhou
 * @date 2022/4/2
 */
@Component
public class DeleteTagManager {

    private final TagMapper tagMapper;

    @Autowired
    public DeleteTagManager(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    @CacheEvict(value = "tag:a", key = "#bookmarkId")
    public boolean deleteTag(String tagName, int bookmarkId) {
        return tagMapper.deleteTag(tagName, bookmarkId);
    }

    @Caching(evict = {
            @CacheEvict(value = "tag:a", key = "#bookmarkId"),
            @CacheEvict(value = "tag:all", allEntries = true),
            @CacheEvict(value = "tag:popular", allEntries = true)
    })
    public void deleteAllTagsByWebId(int bookmarkId) {
        tagMapper.deleteAllTagsByBookmarkId(bookmarkId);
    }
}
