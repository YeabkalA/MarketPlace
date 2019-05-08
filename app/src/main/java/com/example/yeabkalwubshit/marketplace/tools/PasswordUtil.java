package com.example.yeabkalwubshit.marketplace.tools;

/**
 * Validation tool for password inputs.
 */
public class PasswordUtil {
    public static boolean validatePassword(String password1, String password2) {
        return password1.equals(password2);
    }
}
