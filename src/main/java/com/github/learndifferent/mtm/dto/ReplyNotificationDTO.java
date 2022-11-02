package com.github.learndifferent.mtm.dto;

import com.github.learndifferent.mtm.entity.ReplyNotification;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Reply Notification Data Transfer Object
 *
 * @author zhou
 * @date 2021/10/7
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Accessors(chain = true)
public class ReplyNotificationDTO extends ReplyNotification implements Serializable {

    private static final long serialVersionUID = 1L;
}