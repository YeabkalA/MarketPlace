package com.example.yeabkalwubshit.marketplace.tools;

/**
 * Validation tool for phone number inputs.
 */
public class PhoneNumberUtil {
    public static boolean validPhoneNumber(String phoneNumberStr) {

        if(phoneNumberStr.contains("-")) {
            String[] split = phoneNumberStr.split("-");

            if(split.length != 3) return false;

            for(String part: split) {
                try {
                    Integer.parseInt(part);
                }
                catch (Exception e) {
                    return false;
                }
            }

            return split[0].length() == 3
                    && split[1].length() == 3 && split[2].length() == 4;
        }

        if(phoneNumberStr.length() != 10) return false;

        for(char c: phoneNumberStr.toCharArray()) {
            if(!Character.isDigit(c)) return false;
        }
        return true;

    }
}
