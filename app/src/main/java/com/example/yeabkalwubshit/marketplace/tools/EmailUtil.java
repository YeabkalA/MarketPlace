package com.example.yeabkalwubshit.marketplace.tools;

/**
 * Validation tool for email inputs.
 */
public class EmailUtil {
    public static boolean validateEmail(String emailString) {
        if(!emailString.contains("@")) return false;
        String[] splitOnAt = emailString.split("@");
        if(splitOnAt.length != 2) return false;
        String afterAt = splitOnAt[1];
        if(!afterAt.contains(".")) return false;
        String[] onDot = afterAt.split("\\.");
        if(onDot.length != 2) return false;
        return true;
    }

}
