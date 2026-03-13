package com.github.learndifferent.mtm.utils;

import java.util.UUID;

/**
 * UUID utility class
 *
 * @author zhou
 * @date 2021/09/05
 */
public class UUIDUtils {

    private UUIDUtils() {
    }

    /**
     * Get a 32-character UUID
     *
     * @return 32-character UUID
     */
    public static String getUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Get a UUID with specified length
     *
     * @param length Desired length
     * @return UUID with specified length
     */
    public static String getUuid(int length) {
        if (length < 0) {
            length = 0;
        }

        String uuid = getUuid();

        return uuid.substring(0, length);
    }
}
