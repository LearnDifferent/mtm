package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.dto.ReplyNotificationWithMsgDTO;
import com.github.learndifferent.mtm.query.DelReNotificationRequest;
import java.util.List;

/**
 * Notification Service
 *
 * @author zhou
 * @date 2021/09/21
 */
public interface NotificationService {

    /**
     * Count the total number of reply notifications
     *
     * @param receiveUsername username
     * @return total number of reply notifications
     */
    long countReplyNotifications(String receiveUsername);

    /**
     * Count the number of new reply notifications
     *
     * @param receiveUsername username
     * @return number of new reply notifications
     */
    int countNewReplyNotifications(String receiveUsername);

    /**
     * Get reply / comment notifications and clear notification count
     *
     * @param receiveUsername user's name who is about to receive notifications
     * @param to              index of the last element of the reply notification list
     * @return {@link List}<{@link ReplyNotificationWithMsgDTO}> reply / comment notification list
     * @throws com.github.learndifferent.mtm.exception.ServiceException If the user is not current user, this method
     *                                                                  will throw an exception with
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                                                                  and if no results found, the result code will
     *                                                                  be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#NO_RESULTS_FOUND}.
     */
    List<ReplyNotificationWithMsgDTO> getReplyNotifications(String receiveUsername, int to);


    /**
     * Delete reply notification
     *
     * @param data notification data to delete
     * @throws com.github.learndifferent.mtm.exception.ServiceException throw an exception if failure
     */
    void deleteReplyNotification(DelReNotificationRequest data);

    /**
     * Delete all system notifications and remove all saved usernames
     * that read the most recent previous system notifications
     */
    void deleteSysNotificationAndSavedNames();

    /**
     * Get first 20 system notifications and convert the text to an HTML format.
     * <p>This will also record the username of the user who wants to get the latest system notifications</p>
     *
     * @param username username of the user who wants to get the latest system notifications
     * @return first 20 system notifications
     */
    String getSysNotHtmlAndRecordName(String username);

    /**
     * Send System Notification and ensure the limit is 20.
     * <p>This will also delete all saved usernames that
     * read the most recent previous system notifications.</p>
     *
     * @param content content of notification
     */
    void sendSysNotAndDelSavedNames(String content);

    /**
     * Check whether the user has read the latest system notification
     *
     * @param username username of the user
     * @return true if user has read the latest notification, or there is no system notification
     * <p>false if user has not read the latest notification</p>
     */
    boolean checkIfReadLatestSysNotification(String username);

    /**
     * Generate a User Role Change Notification
     *
     * @param userId ID of the user
     * @return User Role Change Notification
     * (It will be an empty string if the user role is not changed)
     */
    String generateRoleChangeNotification(String userId);
}
