package com.github.learndifferent.mtm.annotation.general.notification;

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