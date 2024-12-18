package com.wangguangwu.exchange.utils;

import cn.hutool.crypto.digest.BCrypt;

/**
 * @author wangguangwu
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static boolean validatePassword(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }

    public static String encryptPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    public static boolean isMatch(String rawPassword, String newPassword) {
        return validatePassword(rawPassword, newPassword);
    }
}
