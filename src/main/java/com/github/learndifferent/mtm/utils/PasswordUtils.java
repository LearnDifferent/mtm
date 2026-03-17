package com.github.learndifferent.mtm.utils;

import java.util.regex.Pattern;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password utility with legacy MD5 compatibility for migration.
 */
public final class PasswordUtils {

    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    private static final Pattern LEGACY_MD5_PATTERN = Pattern.compile("^[a-fA-F0-9]{32}$");

    private PasswordUtils() {
    }

    public static String encode(String rawPassword) {
        return PASSWORD_ENCODER.encode(rawPassword);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            return false;
        }

        if (isLegacyHash(encodedPassword)) {
            return Md5Util.getMd5(rawPassword).equalsIgnoreCase(encodedPassword);
        }

        return PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
    }

    public static boolean needsUpgrade(String encodedPassword) {
        return isLegacyHash(encodedPassword);
    }

    private static boolean isLegacyHash(String encodedPassword) {
        return LEGACY_MD5_PATTERN.matcher(encodedPassword).matches();
    }
}
