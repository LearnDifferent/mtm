package com.github.learndifferent.mtm.mapper;

import com.github.learndifferent.mtm.dto.BasicWebDataDTO;
import com.github.learndifferent.mtm.dto.BookmarkFilterDTO;
import com.github.learndifferent.mtm.dto.PopularBookmarkDTO;
import com.github.learndifferent.mtm.dto.search.WebForSearchDTO;
import com.github.learndifferent.mtm.entity.BookmarkDO;
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
    List<BookmarkDO> filterPublicBookmarks(BookmarkFilterDTO filter);

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
     * Get the number of public bookmarks of all users and private bookmarks of specific user
     *
     * @param specUsername username of the user whose private bookmarks will be counted
     * @return the number of bookmarks
     */
    int countAllPublicAndSpecificPrivateBookmarks(String specUsername);

    /**
     * Get the number of bookmarks of the user
     *
     * @param userName             username of the user
     * @param shouldIncludePrivate true if including the private bookmarks
     * @return number of bookmarks of the user
     */
    int countUserBookmarks(@Param("userName") String userName,
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
     * @param userName             username
     * @param from                 from
     *                             <p>The result will not be paginated if {@code from} or {@code size} is null</p>
     * @param size                 size
     *                             <p>The result will not be paginated if {@code from} or {@code size} is null</p>
     * @param shouldIncludePrivate true if include private bookmarks
     * @return A list of {@link BookmarkDO}
     */
    List<BookmarkDO> getUserBookmarks(@Param("userName") String userName,
                                      @Param("from") Integer from,
                                      @Param("size") Integer size,
                                      @Param("shouldIncludePrivate") boolean shouldIncludePrivate);

    /**
     * Get public bookmarks of all users and
     * some private bookmarks of the user whose username is {@code includePrivateUsername},
     * excluding the bookmarks of the user whose username is {@code excludeUsername}
     *
     * @param includePrivateUsername username of the user whose public and private bookmarks will be shown
     * @param excludeUsername        username of the user whose bookmarks will not be shown
     * @param from                   from
     * @param size                   size
     * @return bookmarks
     */
    List<BookmarkDO> getAllPublicSomePrivateExcludingSpecificUserBookmark(
            @Param("includePrivateUsername") String includePrivateUsername,
            @Param("excludeUsername") String excludeUsername,
            @Param("from") int from,
            @Param("size") int size);

    /**
     * Get the number of public bookmarks of all users and
     * some private bookmarks of specific user whose username is {@code includePrivateUsername},
     * excluding the bookmarks of the user whose username is {@code excludeUsername}
     *
     * @param includePrivateUsername username of the user whose public and private bookmarks will be counted
     * @param excludeUsername        username of the user whose bookmarks will not be counted
     * @return the number of bookmarks
     */
    int countAllPublicSomePrivateExcludingSpecificUserBookmark(
            @Param("includePrivateUsername") String includePrivateUsername,
            @Param("excludeUsername") String excludeUsername);

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
     * @param username username
     * @param url      URL
     * @return true if the user has already bookmarked the web page
     */
    boolean checkIfUserBookmarked(@Param("username") String username, @Param("url") String url);

    /**
     * Get public bookmarks of all users and private bookmarks of specific user
     * <p>
     * The result will not be paginated if {@code from} or {@code size} is null
     * </p>
     *
     * @param from         from
     *                     <p>The result will not be paginated if {@code from} or {@code size} is null</p>
     * @param size         size
     *                     <p>The result will not be paginated if {@code from} or {@code size} is null</p>
     * @param specUsername username of the user whose public and private bookmarks will be shown
     * @return public bookmarks of all users and private bookmarks of specific user
     */
    List<BookmarkDO> getAllPublicAndSpecificPrivateBookmarks(@Param("from") Integer from,
                                                             @Param("size") Integer size,
                                                             @Param("specUsername") String specUsername);

    /**
     * Delete a bookmark
     *
     * @param id ID of the bookmark
     * @return true if success
     */
    boolean deleteBookmarkById(Integer id);

    /**
     * Delete all bookmarks of the user
     *
     * @param userName username of the user
     */
    void deleteUserBookmarks(String userName);

    /**
     * Add the website to bookmarks
     *
     * @param newBookmark the website to be bookmarked
     * @return true if success
     */
    boolean addBookmark(BookmarkDO newBookmark);

    /**
     * Update bookmark
     *
     * @param updatedBookmark updated bookmark data
     * @return true if success
     */
    boolean updateBookmark(BookmarkDO updatedBookmark);

    /**
     * Get the username of the user who owns the bookmark
     *
     * @param id ID of the bookmark
     * @return username of the user who owns the bookmark
     */
    String getBookmarkOwnerName(int id);

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
}