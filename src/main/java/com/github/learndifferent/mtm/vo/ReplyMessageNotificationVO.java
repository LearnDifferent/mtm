package com.github.learndifferent.mtm.vo;

import com.github.learndifferent.mtm.entity.ReplyNotification;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Notification with reply message
 *
 * @author zhou
 * @date 2022/4/8
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Accessors(chain = true)
public class ReplyMessageNotificationVO extends ReplyNotification implements Serializable {

    /**
     * Extend parameter: reply message
     * <p>Null if the bookmark, comment or reply has been deleted</p>
     */
    private String message;

    private static final long serialVersionUID = 1L;
}