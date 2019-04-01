package com.example.yeabkalwubshit.marketplace;

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

        try {
            Integer.parseInt(phoneNumberStr);
        }
        catch (Exception e) {
            return false;
        }
        return phoneNumberStr.length() == 10;

    }
}
