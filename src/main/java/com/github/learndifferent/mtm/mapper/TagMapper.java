package com.github.learndifferent.mtm.mapper;

import com.github.learndifferent.mtm.dto.search.TagForSearchDTO;
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
     * Check if the tag exists
     *
     * @param tag        tag
     * @param bookmarkId Bookmark ID
     * @return true if the tag exists
     */
    boolean checkIfTagExists(@Param("tag") String tag,
                             @Param("bookmarkId") long bookmarkId);

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
    boolean deleteTag(@Param("tagName") String tagName, @Param("bookmarkId") long bookmarkId);

    /**
     * Delete all tags of a bookmark
     *
     * @param bookmarkId ID of the bookmark
     */
    void deleteAllTagsByBookmarkId(long bookmarkId);

    /**
     * Get all the tags and count the numbers of public bookmarks
     *
     * @return a list of tags and count the numbers of public bookmarks
     */
    List<TagAndCountDO> getAllTagsAndCountOfPublicBookmarks();

    /**
     * Get popular tags
     *
     * @param userId user ID of the user who is searching for popular tags
     * @param from   from
     * @param size   size
     * @return a list of paginated tags, which are the tags of public bookmarks or the tags of
     * bookmarks that owns by the user who is searching for popular tags, that appear more than once.
     */
    List<TagAndCountDO> getPopularTags(@Param("userId") long userId,
                                       @Param("from") int from,
                                       @Param("size") int size);

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

    /**
     * Search tag data by keyword within an range of number
     *
     * @param keyword   keyword
     * @param userId    user ID of the user who is searching the tag
     * @param rangeFrom greater than or equal to the number of bookmarks of this tag.
     *                  don't filter if {@code rangeFrom} or {@code rangeTo} is null.
     * @param rangeTo   less than or equal to the number of bookmarks of this tag
     *                  don't filter if {@code rangeFrom} or {@code rangeTo} is null.
     * @param from      from
     * @param size      size
     * @return tag data
     */
    List<TagForSearchDTO> searchTagDataByKeywordAndRange(@Param("keyword") String keyword,
                                                         @Param("userId") long userId,
                                                         @Param("rangeFrom") Integer rangeFrom,
                                                         @Param("rangeTo") Integer rangeTo,
                                                         @Param("from") int from,
                                                         @Param("size") int size);

    /**
     * Count the number of tags by keyword within an range of number
     *
     * @param keyword   keyword
     * @param userId    user ID of the user who is searching the tag
     * @param rangeFrom greater than or equal to the number of bookmarks of this tag.
     *                  don't filter if {@code rangeFrom} or {@code rangeTo} is null.
     * @param rangeTo   less than or equal to the number of bookmarks of this tag
     *                  don't filter if {@code rangeFrom} or {@code rangeTo} is null.
     * @return the number of tags
     */
    long countTagDataByKeywordAndRange(@Param("keyword") String keyword,
                                       @Param("userId") long userId,
                                       @Param("rangeFrom") Integer rangeFrom,
                                       @Param("rangeTo") Integer rangeTo);
}