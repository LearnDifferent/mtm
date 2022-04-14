package com.github.learndifferent.mtm.annotation.general.notification;

import com.github.learndifferent.mtm.constant.enums.PriorityLevel;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Send System Notification
 *
 * @author zhou
 * @date 2022/4/12
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SystemNotification {

    /**
     * Priority level
     *
     * @return Priority level
     */
    PriorityLevel priority() default PriorityLevel.LOW;

    /**
     * Message type
     *
     * @return message type
     */
    MessageType messageType();

    enum MessageType {
        /**
         * A new user has been created
         */
        NEW_USER,
        /**
         * User logged in
         */
        LOGIN,
        /**
         * User logged out
         */
        LOGOUT
    }
}