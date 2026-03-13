package com.github.learndifferent.mtm.utils;

import org.springframework.util.DigestUtils;

/**
 * MD5 utility class
 *
 * @author From the internet, with simple modifications
 * @date 2021/09/05
 */
public class Md5Util {

    private Md5Util() {
    }

    public static String getMd5(String str) {
        String salt = "1234mtm4321";
        String base = str + "/" + salt;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }

}