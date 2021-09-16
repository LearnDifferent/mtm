package com.github.learndifferent.mtm.utils;

import org.springframework.util.DigestUtils;

/**
 * MD5 工具
 *
 * @author 来自网络，进行了简单修改
 * @date 2021/09/05
 */
public class Md5Util {

    private static final String SALT = "acDn156";

    public static String getMd5(String str) {
        String base = str + "/" + SALT;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }

}