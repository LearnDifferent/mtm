package com.github.learndifferent.mtm.mapper;

import com.github.learndifferent.mtm.dto.CommentDTO;
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
    CommentDTO getCommentById(long id);

    /**
     * Retrieve the ID of the user who sent the comment
     *
     * @param commentId ID of the comment
     * @return ID of the user who sent the comment
     */
    Long getCommentSenderUserId(long commentId);

    /**
     * Check if a comment is present
     *
     * @param comment    comment
     * @param bookmarkId bookmark ID
     * @param userId     user ID
     * @return true if the comment is present
     */
    boolean checkIfCommentPresent(@Param("comment") String comment,
                                  @Param("bookmarkId") long bookmarkId,
                                  @Param("userId") long userId);

    /**
     * Check if a comment is present by comment ID
     *
     * @param id Comment ID
     * @return true if the comment is present
     */
    boolean checkIfCommentPresentById(@Param("id") long id);

    /**
     * Get user ID of the comment by comment ID
     *
     * @param id Comment ID
     * @return If a comment exists, return the user ID of that comment.
     * If the comment does not exist, return null.
     */
    Long getCommentUserIdByCommentId(long id);

    /**
     * Get the text of the comment
     *
     * @param id ID of the comment
     * @return the text of the comment
     */
    String getCommentTextById(long id);

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
    List<CommentDTO> getBookmarkComments(@Param("bookmarkId") long bookmarkId,
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
    long countRepliesFromComment(long countRepliesFromCommentId);

    /**
     * Delete a comment by id
     *
     * @param id ID of the comment
     * @return true if success
     */
    boolean deleteCommentById(long id);

    /**
     * Delete comments of the user
     *
     * @param userId user ID of the user
     */
    void deleteCommentsByUserId(long userId);

    /**
     * Add a comment
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
    boolean updateComment(@Param("id") long id, @Param("comment") String comment);

    /**
     * Get the number of comments (exclude replies) of a bookmark
     *
     * @param bookmarkId ID of the bookmark
     * @return number of comments of the bookmarked website
     */
    long countCommentByBookmarkId(long bookmarkId);
}