package com.github.learndifferent.mtm.query;

import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.ActionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Check permission
 *
 * @author zhou
 * @date 2023/10/12
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PermissionCheckRequest {

    /**
     * Action type
     */
    private ActionType actionType;

    /**
     * ID
     */
    private Long bookmarkId;

    /**
     * User ID
     */
    private Long userId;

    /**
     * Tag
     */
    private String tag;

    /**
     * Comment ID
     */
    private Long commentId;

    /**
     * Comment
     */
    private String comment;

    /**
     * Reply to comment ID
     */
    private Long replyToCommentId;
}