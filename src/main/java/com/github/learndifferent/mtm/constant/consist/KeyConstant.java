package com.github.learndifferent.mtm.constant.consist;

/**
 * Redis 的 key 相关常量
 *
 * @author zhou
 * @date 2021/09/05
 */
public final class KeyConstant {

    private KeyConstant() {
    }

    public static final String SYSTEM_NOTIFICATION = "systemNotification";

    /**
     * The key of reply notification is "notification:" + lowercase username
     */
    public static final String REPLY_NOTIFICATION_PREFIX = "notification:";
}
