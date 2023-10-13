package com.github.learndifferent.mtm.mapper;

import com.github.learndifferent.mtm.entity.CommentDO;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * CommentMapper
 *
 * @author zhou
 * @date 2021/9/28
 */
@Repository
public interface CommentMapper {

    /**
     * Get a comment by id
     *
     * @param id ID of the comment
     * @return the comment
     */
    CommentDO getCommentById(int id);

    /**
     * Retrieve the ID of the user who sent the comment
     *
     * @param commentId ID of the comment
     * @return ID of the user who sent the comment
     */
    Long getCommentSenderUserId(int commentId);

    /**
     * Check if a comment exists
     *
     * @param comment    comment content
     * @param bookmarkId ID of the bookmark
     * @param username   username of the user who sent the comment
     * @return true if a comment exists
     */
    boolean checkIfCommentExists(@Param("comment") String comment,
                                 @Param("bookmarkId") int bookmarkId,
                                 @Param("username") String username);

    /**
     * Get the text of the comment
     *
     * @param id ID of the comment
     * @return the text of the comment
     */
    String getCommentTextById(int id);

    /**
     * Get comments of a bookmark
     *
     * @param bookmarkId       ID of the bookmark
     * @param replyToCommentId ID of the comment to reply
     *                         <p>
     *                         null if this is not a reply
     *                         </p>
     * @param load             Amount of data to load
     * @param isDesc           True if descending order
     * @return the comments
     */
    List<CommentDO> getBookmarkComments(@Param("bookmarkId") long bookmarkId,
                                        @Param("replyToCommentId") Long replyToCommentId,
                                        @Param("load") Integer load,
                                        @Param("isDesc") Boolean isDesc);

    /**
     * Get a count of the replies from this comment
     * <p>How many replies are pointed at this {@code countRepliesFromCommentId}</p>
     *
     * @param countRepliesFromCommentId Count the replies from this comment
     * @return a count of the replies from a comment
     */
    int countRepliesFromComment(int countRepliesFromCommentId);

    /**
     * Delete a comment by id
     *
     * @param id ID of the comment
     * @return true if success
     */
    boolean deleteCommentById(int id);

    /**
     * Delete comments of the user
     *
     * @param username username of the user
     */
    void deleteCommentsByUsername(String username);

    /**
     * Add a comment (this method is using generated keys)
     *
     * @param comment comment
     * @return true if success
     */
    boolean addComment(CommentDO comment);

    /**
     * Update a comment
     *
     * @param id      ID of the comment
     * @param comment comment
     * @return true if success
     */
    boolean updateComment(@Param("id") int id, @Param("comment") String comment);

    /**
     * Get the number of comments (exclude replies) of a bookmark
     *
     * @param bookmarkId ID of the bookmark
     * @return number of comments of the bookmarked website
     */
    int countCommentByBookmarkId(int bookmarkId);
}