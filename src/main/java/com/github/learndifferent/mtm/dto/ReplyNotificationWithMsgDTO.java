package com.github.learndifferent.mtm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Notification with comment / reply message
 *
 * @author zhou
 * @date 2021/10/10
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplyNotificationWithMsgDTO extends ReplyNotificationDTO {

    /**
     * New field: comment / reply message  (null if the comment or reply has been deleted)
     */
    private String message;
}
