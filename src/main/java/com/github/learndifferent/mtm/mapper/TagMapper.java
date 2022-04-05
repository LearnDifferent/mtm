package com.github.learndifferent.mtm.mapper;

import com.github.learndifferent.mtm.entity.TagAndCountDO;
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
     * Get the tags by {@code webId}.
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
     * Get tags and count the numbers of bookmarks of tags.
     * The result will not be paginated if the {@code size} is less than 0.
     *
     * @param from          from
     * @param size          size
     * @param beMoreThanOne true if the numbers should be greater than 1
     * @return a list of popular tags
     */
    List<TagAndCountDO> getTagAndCount(@Param("from") int from,
                                       @Param("size") int size,
                                       @Param("beMoreThanOne") boolean beMoreThanOne);

    /**
     * Get the number of unique tags
     *
     * @return number of unique tags
     */
    int countDistinctTags();
}