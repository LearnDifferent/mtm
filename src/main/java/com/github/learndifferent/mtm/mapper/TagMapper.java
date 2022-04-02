package com.github.learndifferent.mtm.mapper;

import com.github.learndifferent.mtm.dto.PopularTagDTO;
import com.github.learndifferent.mtm.entity.TagDO;
import java.util.List;
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
     * Get a specific tag by name of the tag and Web ID
     *
     * @param tagName name of the tag
     * @param webId   ID of the bookmarked website data that the tag applied to
     * @return tag
     */
    TagDO getSpecificTagByTagTextAndWebId(@Param("tagName") String tagName, @Param("webId") int webId);

    /**
     * Get the tags by Web ID
     * <p>
     * Get all tags if the ID is null.
     * </p>
     *
     * @param webId Web ID
     * @param from  from
     * @param size  size
     * @return tags
     */
    List<String> getTagsByWebId(@Param("webId") Integer webId, @Param("from") int from, @Param("size") int size);

    /**
     * Get Web ID by Tag Name
     *
     * @param tagName name of the tag
     * @param from    from
     * @param size    size
     * @return Web IDs
     */
    List<Integer> getWebIdByTagName(@Param("tagName") String tagName,
                                    @Param("from") int from,
                                    @Param("size") int size);

    /**
     * Delete a tag
     *
     * @param tagName name of the tag to be deleted
     * @param webId   ID of the bookmarked website data that the tag applied to
     * @return True if success. False if failure or the tag does not exist.
     */
    boolean deleteTag(@Param("tagName") String tagName, @Param("webId") int webId);

    /**
     * Delete all tags of a bookmarked website
     *
     * @param webId ID of the bookmarked website data
     */
    void deleteAllTags(int webId);

    /**
     * Get popular tags
     *
     * @param from from
     * @param size size
     * @return a list of popular tags
     */
    List<PopularTagDTO> getPopularTags(@Param("from") int from, @Param("size") int size);
}