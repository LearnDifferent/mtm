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
     * Get a specific tag by name of the tag and Bookmark ID
     *
     * @param tagName    name of the tag
     * @param bookmarkId ID of the bookmark that the tag applied to
     * @return tag
     */
    TagDO getSpecificTagByTagTextAndBookmarkId(@Param("tagName") String tagName, @Param("bookmarkId") int bookmarkId);

    /**
     * Get all tags
     *
     * @param from from
     * @param size size
     * @return tags
     */
    List<String> getAllTags(@Param("from") int from,
                            @Param("size") int size);

    /**
     * Get the tags by {@code bookmarkId}.
     *
     * @param bookmarkId ID of the bookmark
     * @param from       from
     * @param size       size
     * @return tags
     */
    List<String> getTagsByBookmarkId(@Param("bookmarkId") Integer bookmarkId,
                                     @Param("from") int from,
                                     @Param("size") int size);

    /**
     * Get Bookmark IDs by Tag Name
     *
     * @param tagName name of the tag
     * @param from    from
     * @param size    size
     * @return Bookmark IDs
     */
    List<Integer> getBookmarkIdsByTagName(@Param("tagName") String tagName,
                                          @Param("from") int from,
                                          @Param("size") int size);

    /**
     * Delete a tag
     *
     * @param tagName    name of the tag to be deleted
     * @param bookmarkId ID of the bookmark that the tag applied to
     * @return True if success. False if failure or the tag does not exist.
     */
    boolean deleteTag(@Param("tagName") String tagName, @Param("bookmarkId") int bookmarkId);

    /**
     * Delete all tags of a bookmark
     *
     * @param bookmarkId ID of the bookmark
     */
    void deleteAllTagsByBookmarkId(int bookmarkId);

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

    /**
     * Count the number of times a tag appears in database
     *
     * @param tagName The name of the tag to count.
     * @return The number of times the tag appears in database
     */
    int countTags(String tagName);
}