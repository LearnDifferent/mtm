package com.github.learndifferent.mtm.mapper;

import com.github.learndifferent.mtm.entity.CommentDO;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
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
     * Gets comment by web id, username and comment
     *
     * @param comment  comment
     * @param webId    web id
     * @param username username
     * @return {@link CommentDO} comment data object
     */
    CommentDO getCommentByWebIdAndUsernameAndComment(@Param("comment") String comment,
                                                     @Param("webId") int webId,
                                                     @Param("username") String username);

    /**
     * Get comments by web id
     *
     * @param webId  Web ID
     * @param load   Amount of data to load
     * @param isDesc True if descending order
     * @return the comments
     */
    List<CommentDO> getCommentsByWebId(@Param("webId") Integer webId,
                                       @Param("load") Integer load,
                                       @Param("isDesc") Boolean isDesc);

    /**
     * Gets comments by username
     *
     * @param username username
     * @return the comments
     */
    List<CommentDO> getCommentsByUsername(String username);

    /**
     * Delete a comment by id
     *
     * @param commentId comment id
     * @return success or failure
     */
    boolean deleteCommentById(int commentId);

    /**
     * Add a comment
     *
     * @param comment comment
     * @return success or failure
     */
    boolean addComment(CommentDO comment);

    /**
     * Update a comment
     *
     * @param commentId comment id
     * @param comment   comment
     * @return success or failure
     */
    boolean updateComment(@Param("commentId") int commentId, @Param("comment") String comment);

    /**
     * Get count of website comments
     *
     * @param webId web id
     * @return count of website comments
     */
    Integer countCommentByWebId(Integer webId);
}
