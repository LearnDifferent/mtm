package com.github.learndifferent.mtm.mapper;

import com.github.learndifferent.mtm.dto.BookmarkFilterDTO;
import com.github.learndifferent.mtm.dto.PopularBookmarkDTO;
import com.github.learndifferent.mtm.dto.search.WebForSearchDTO;
import com.github.learndifferent.mtm.entity.WebsiteDO;
import com.github.learndifferent.mtm.vo.VisitedBookmarksVO;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * Website Mapper
 *
 * @author zhou
 * @date 2021/09/05
 */
@Repository
public interface WebsiteMapper {

    /**
     * Filter public bookmarked websites
     *
     * @param filter the filter
     * @return filtered bookmarked websites
     */
    List<WebsiteDO> filterPublicBookmarks(BookmarkFilterDTO filter);

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
     * Get all public basic website data for search
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
     * @param shouldIncludePrivate true if include private website data
     * @return A list of {@link WebsiteDO}
     */
    List<WebsiteDO> getUserBookmarks(@Param("userName") String userName,
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
    List<WebsiteDO> getAllPublicSomePrivateExcludingSpecificUserBookmark(
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
     * Get all bookmarks that have the given URL
     *
     * @param url URL
     * @return {@code List<WebsiteDO>}
     */
    List<WebsiteDO> getBookmarksByUrl(String url);

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
    List<WebsiteDO> getAllPublicAndSpecificPrivateBookmarks(@Param("from") Integer from,
                                                            @Param("size") Integer size,
                                                            @Param("specUsername") String specUsername);

    /**
     * Delete a bookmarked website
     *
     * @param webId ID of the bookmarked website data
     * @return true if success
     */
    boolean deleteBookmarkById(Integer webId);

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
    boolean addBookmark(WebsiteDO newBookmark);

    /**
     * Find the bookmark by ID
     *
     * @param webId ID of the bookmarked website data
     * @return {@link WebsiteDO}
     */
    WebsiteDO getBookmarkById(Integer webId);

    /**
     * Update bookmark
     *
     * @param updatedBookmark updated bookmark data
     * @return true if success
     */
    boolean updateBookmark(WebsiteDO updatedBookmark);

    /**
     * Get the username of the user who owns the bookmark
     *
     * @param webId ID of the bookmarked website data
     * @return username of the user who owns the bookmark
     */
    String getBookmarkOwnerName(int webId);

    /**
     * Get visited bookmarks
     *
     * @param from from
     * @param size size
     * @return all visited bookmarks
     */
    List<VisitedBookmarksVO> getVisitedBookmarks(@Param("from") int from, @Param("size") int size);
}