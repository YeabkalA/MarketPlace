package com.example.yeabkalwubshit.marketplace;

public class PhoneNumberUtil {
    public static boolean validPhoneNumber(String phoneNumberStr) {

        if(phoneNumberStr.contains("-")) {
            String[] split = phoneNumberStr.split("-");

            if(split.length != 3) return false;

            try {
                Integer.parseInt(split[0]);
            }
            catch (Exception e) {
                return false;
            }

            try {
                Integer.parseInt(split[1]);
            }
            catch (Exception e) {
                return false;
            }

            try {
                Integer.parseInt(split[2]);
            }
            catch (Exception e) {
                return false;
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
