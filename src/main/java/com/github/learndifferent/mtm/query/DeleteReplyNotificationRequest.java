package com.github.learndifferent.mtm.query;

import com.github.learndifferent.mtm.entity.ReplyNotification;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Request body that contains the data of the reply notification to delete
 *
 * @author zhou
 * @date 2021/10/10
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Accessors(chain = true)
@NoArgsConstructor
public class DeleteReplyNotificationRequest extends ReplyNotification implements Serializable {

    private static final long serialVersionUID = 1L;
}