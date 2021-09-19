package com.github.learndifferent.mtm.utils;

import java.util.UUID;

/**
 * UUID 工具
 *
 * @author zhou
 * @date 2021/09/05
 */
public class UUIDUtils {

    /**
     * 32 位默认长度的uuid
     *
     * @return 32 位的 uuid
     */
    public static String getUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取指定长度uuid
     *
     * @param length 指定长度
     * @return 指定长度uuid
     */
    public static String getUuid(int length) {
        if (length < 0) {
            length = 0;
        }

        String uuid = getUuid();

        return uuid.substring(0, length);
    }
}
