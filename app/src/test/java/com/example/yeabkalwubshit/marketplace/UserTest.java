package com.example.yeabkalwubshit.marketplace;

import com.example.yeabkalwubshit.marketplace.objects.Address;
import com.example.yeabkalwubshit.marketplace.objects.User;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserTest {
    static String FIRST_NAME = "TestFirstName";
    static String LAST_NAME = "TestLastName";
    static String EMAIL = "test@email.com";
    static String PHONE_NUMBER = "123-456-7876";

    static String LINE1 = "1234 line1";
    static String CITY = "test_city";
    static String STATE = "AA";
    static String COUNTRY = "test_country";
    static String GOOD_ZIP = "75024";
    static String BAD_ZIP = "01";


    static Address GOOD_ADDRESS;
    static Address BAD_ADDRESS;

    @Before
    public void initAdddress() {
        GOOD_ADDRESS = new Address.Builder()
                .setLine1(LINE1)
                .setCity(CITY)
                .setState(STATE)
                .setCountry(COUNTRY)
                .setZip(GOOD_ZIP)
                .build();
        BAD_ADDRESS = new Address.Builder()
                .setLine1(LINE1)
                .setCity(CITY)
                .setState(STATE)
                .setCountry(COUNTRY)
                .setZip(BAD_ZIP)
                .build();

    }

    @Test
    public void successfulUserCreationTest() {
        User user = new User.Builder()
                .setFirstName(FIRST_NAME)
                .setLastName(LAST_NAME)
                .setAddress(GOOD_ADDRESS)
                .setEmail(EMAIL)
                .setPhoneNumber(PHONE_NUMBER)
                .build();
        assertTrue(user.isValid());
    }

    @Test
    public void unsuccessfulUserCreationTest() {
        User user = new User.Builder()
                .setFirstName(FIRST_NAME)
                .setLastName(LAST_NAME)
                .setAddress(BAD_ADDRESS)
                .setEmail(EMAIL)
                .setPhoneNumber(PHONE_NUMBER)
                .build();
        assertFalse(user.isValid());
    }
}