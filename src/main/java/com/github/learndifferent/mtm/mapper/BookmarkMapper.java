package com.github.learndifferent.mtm.mapper;

import com.github.learndifferent.mtm.dto.BasicWebDataDTO;
import com.github.learndifferent.mtm.dto.BookmarkFilterDTO;
import com.github.learndifferent.mtm.dto.NewBookmarkDTO;
import com.github.learndifferent.mtm.dto.PopularBookmarkDTO;
import com.github.learndifferent.mtm.dto.search.WebForSearchDTO;
import com.github.learndifferent.mtm.entity.BookmarkDO;
import com.github.learndifferent.mtm.vo.BookmarkVO;
import com.github.learndifferent.mtm.vo.VisitedBookmarkVO;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * Bookmark Mapper
 *
 * @author zhou
 * @date 2021/09/05
 */
@Repository
public interface BookmarkMapper {

    /**
     * Filter public bookmark
     *
     * @param filter the filter
     * @return filtered bookmarks
     */
    List<BookmarkVO> filterPublicBookmarks(BookmarkFilterDTO filter);

    /**
     * Get the number of unique public URLs
     *
     * @return number of unique public URLs
     */
    int countDistinctPublicUrl();

    /**
     * Get popular bookmarks
     *
     * @param from from
     * @param size size
     * @return {@code List<PopularBookmarkDTO>}
     */
    List<PopularBookmarkDTO> getPopularPublicBookmarks(@Param("from") int from, @Param("size") int size);

    /**
     * Get all public basic bookmark for search
     *
     * @return {@code List<WebForSearchDTO>}
     */
    List<WebForSearchDTO> getAllPublicBasicWebDataForSearch();

    /**
     * Get the count of public bookmarks for all users and private bookmarks for a specific user
     *
     * @param userId User ID of the user whose private bookmarks will be counted
     * @return The count of bookmarks
     */
    int countPublicAndUserOwnedPrivateBookmarks(long userId);

    /**
     * Get public bookmarks and private bookmarks for a specific user.
     *
     * @param from   from
     * @param size   size
     * @param userId user ID of the user whose private bookmarks will also be shown
     * @return public bookmarks of all users and private bookmarks for a specific user
     */
    List<BookmarkVO> getPublicAndUserOwnedPrivateBookmarks(@Param("from") Integer from,
                                                           @Param("size") Integer size,
                                                           @Param("userId") long userId);

    /**
     * Filter bookmarks by username
     * Return public bookmarks of all users, and private bookmarks of a specified user
     * Exclude all bookmarks of a specified user
     *
     * @param privateUserId the user ID of the specified user, whose public and private bookmarks will be returned
     * @param excludeUserId the user ID of the excluded user, whose bookmarks will not be returned
     * @param from          the pagination offset
     * @param size          the pagination limit
     * @return a list of bookmarks
     */
    List<BookmarkVO> filterBookmarksByCriteria(@Param("privateUserId") long privateUserId,
                                               @Param("excludeUserId") long excludeUserId,
                                               @Param("from") int from,
                                               @Param("size") int size);

    /**
     * Get the number of public bookmarks of all users and
     * some private bookmarks of specific user, excluding the bookmarks of the specific user
     *
     * @param privateUserId user ID of the user whose public and private bookmarks will be counted
     * @param excludeUserId user ID of the user whose bookmarks will not be counted
     * @return the number of bookmarks
     */
    int countBookmarkByCriteria(@Param("privateUserId") long privateUserId,
                                @Param("excludeUserId") long excludeUserId);

    /**
     * Get the number of bookmarks of the user
     *
     * @param userId               user ID of the user
     * @param shouldIncludePrivate true if including the private bookmarks
     * @return number of bookmarks of the user
     */
    int countUserBookmarks(@Param("userId") long userId,
                           @Param("shouldIncludePrivate") boolean shouldIncludePrivate);

    /**
     * Get public bookmarks of a user
     * <p>
     * Include all private bookmarks if {@code shouldIncludePrivate} is true
     * </p>
     * <p>
     * The result will not be paginated if {@code from} or {@code size} is null
     * </p>
     *
     * @param userId               ID of the user
     * @param from                 from
     *                             <p>The result will not be paginated if {@code from} or {@code size} is null</p>
     * @param size                 size
     *                             <p>The result will not be paginated if {@code from} or {@code size} is null</p>
     * @param shouldIncludePrivate true if include private bookmarks
     * @return A list of {@link BookmarkDO}
     */
    List<BookmarkVO> getUserBookmarks(@Param("userId") long userId,
                                      @Param("from") Integer from,
                                      @Param("size") Integer size,
                                      @Param("shouldIncludePrivate") boolean shouldIncludePrivate);


    /**
     * Retrieve the bookmark data associated with the provided URL
     *
     * @param url URL
     * @return {@link BasicWebDataDTO}
     */
    BasicWebDataDTO getBookmarkDataByUrl(String url);

    /**
     * Check if the user has already bookmarked the web page
     *
     * @param userId user ID
     * @param url    URL
     * @return true if the user has already bookmarked the web page
     */
    boolean checkIfUserBookmarked(@Param("userId") long userId, @Param("url") String url);

    /**
     * Delete a bookmark
     *
     * @param id ID of the bookmark
     * @return true if success
     */
    boolean deleteBookmarkById(Integer id);

    /**
     * Delete user's all bookmarks
     *
     * @param userId user ID of the user
     */
    void deleteUserBookmarks(long userId);

    /**
     * Add the website to bookmarks
     *
     * @param newBookmark the website to be bookmarked
     * @return true if success
     */
    boolean addBookmark(NewBookmarkDTO newBookmark);

    /**
     * Update bookmark
     *
     * @param updatedBookmark updated bookmark data
     * @return true if success
     */
    boolean updateBookmark(BookmarkDO updatedBookmark);

    /**
     * Retrieve the user ID of the bookmark owner
     *
     * @param bookmarkId ID of the bookmark
     * @return user ID of the bookmark owner
     */
    Long getBookmarkOwnerUserId(int bookmarkId);

    /**
     * Get visited bookmarks
     *
     * @param from from
     * @param size size
     * @return all visited bookmarks
     */
    List<VisitedBookmarkVO> getVisitedBookmarks(@Param("from") int from, @Param("size") int size);

    /**
     * Search website data by keyword
     *
     * @param keyword keyword
     * @param from    from
     * @param size    size
     * @return website data
     */
    List<WebForSearchDTO> searchWebDataByKeyword(@Param("keyword") String keyword,
                                                 @Param("from") int from,
                                                 @Param("size") int size);

    /**
     * get the number of website data by keyword
     *
     * @param keyword keyword
     * @return the number of website data
     */
    long countWebDataByKeyword(String keyword);

    /**
     * Retrieve the bookmark by ID
     *
     * @param id ID
     * @return bookmark
     */
    BookmarkDO getBookmarkById(Integer id);

    /**
     * Retrieve the bookmark by ID
     *
     * @param id ID
     * @return bookmark
     */
    BookmarkVO getBookmarkWithUsernameById(Integer id);

    /**
     * Check if the bookmark exists, bookmark has not been deleted
     * and user has the permission of the bookmark
     *
     * @param bookmarkId ID of the bookmark
     * @param userId     ID of the user
     * @return return true if the bookmark exists and has not been deleted
     */
    boolean checkIfBookmarkAvailable(@Param("bookmarkId") Integer bookmarkId,
                                     @Param("userId") Long userId);
}