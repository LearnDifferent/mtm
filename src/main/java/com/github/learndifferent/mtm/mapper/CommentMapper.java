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
     * @param commentId comment id
     * @return the comment
     */
    CommentDO getCommentById(int commentId);

    /**
     * Get the user's name, who sent the comment
     *
     * @param commentId comment id
     * @return {@link String}
     */
    String getUsernameByCommentId(int commentId);

    /**
     * Get comment by web id, username and comment
     *
     * @param comment  comment
     * @param webId    ID of the bookmarked website data
     * @param username username
     * @return {@link CommentDO} comment data object
     */
    CommentDO getCommentByWebIdAndUsernameAndComment(@Param("comment") String comment,
                                                     @Param("webId") int webId,
                                                     @Param("username") String username);

    /**
     * Get the text of the comment
     *
     * @param commentId comment id
     * @return the text of the comment
     */
    String getCommentTextById(int commentId);

    /**
     * Get comments by {@code webId} and {@code replyToCommentId}.
     * If the {@code webId} is null, get all.
     *
     * @param webId            ID of the bookmarked website data
     * @param replyToCommentId Reply to the comment (null if it's not a reply)
     * @param load             Amount of data to load
     * @param isDesc           True if descending order
     * @return the comments
     */
    List<CommentDO> getCommentsByWebAndReplyCommentId(@Param("webId") Integer webId,
                                                      @Param("replyToCommentId") Integer replyToCommentId,
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
     * @param commentId comment id
     * @return true if success
     */
    boolean deleteCommentById(int commentId);

    /**
     * Delete user's comments
     *
     * @param username username
     */
    void deleteCommentsByUsername(String username);

    /**
     * Add a comment and get new {@link CommentDO} with the ID
     *
     * @param comment comment
     * @return true if success (the id will set to {@link CommentDO} automatically)
     */
    boolean addCommentAndGetId(CommentDO comment);

    /**
     * Update a comment
     *
     * @param commentId comment id
     * @param comment   comment
     * @return true if success
     */
    boolean updateComment(@Param("commentId") int commentId, @Param("comment") String comment);

    /**
     * Get the number of comments (exclude replies) of a bookmarked website
     *
     * @param webId ID of the bookmarked website data
     * @return number of comments of the bookmarked website
     */
    int countCommentByWebId(int webId);
}
