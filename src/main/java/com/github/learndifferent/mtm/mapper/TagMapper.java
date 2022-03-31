package com.github.learndifferent.mtm.mapper;

import com.github.learndifferent.mtm.entity.TagDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * Tag Mapper
 *
 * @author zhou
 * @date 2022/3/31
 */
@Repository
public interface TagMapper {

    /**
     * Add a new tag data
     *
     * @param tag tag data
     * @return true if success
     */
    boolean addTag(TagDO tag);

    /**
     * Get a specific tag by its tag text and Web ID
     *
     * @param tagText text of the tag
     * @param webId   ID of the bookmarked website data that tag applied to
     * @return tag
     */
    TagDO getSpecificTagByTagTextAndWebId(@Param("tagText") String tagText, @Param("webId") int webId);
}