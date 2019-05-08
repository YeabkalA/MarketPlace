package com.example.yeabkalwubshit.marketplace;

import com.example.yeabkalwubshit.marketplace.objects.Address;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AddressTest {
    static String LINE1 = "1234 line1";
    static String LINE2 = "1234 line2";
    static String CITY = "test_city";
    static String STATE = "AA";
    static String COUNTRY = "test_country";
    static String ZIP1 = "75024";
    static String ZIP2 = "75062";
    static String BAD_ZIP_WRONG_LENGTH = "0005";
    static String BAD_ZIP_OUT_OF_RANGE= "00001";

    @Test
    public void successfulAddressCreation() {
       Address address = new Address.Builder()
               .setLine1(LINE1)
               .setLine2(LINE2)
               .setCity(CITY)
               .setState(STATE)
               .setCountry(COUNTRY)
               .setZip(ZIP1)
               .build();

       assertTrue(address.isValid());

       assertTrue(address.getLine1().equals(LINE1));
       assertTrue(address.getLine2().equals(LINE2));
       assertTrue(address.getCity().equals(CITY));
       assertTrue(address.getState().equals(STATE));
       assertTrue(address.getCountry().equals(COUNTRY));
       assertTrue(address.getZip().equals(ZIP1));
    }

    @Test
    public void failedAddressCreation_EmptyFields() {
        Address address = new Address.Builder()
                .setLine1(LINE1)
                .setLine2(LINE2)
                .setZip(ZIP1)
                .build();
        assertFalse(address.isValid());
    }
    @Test
    public void failedAddressCreation_ShortZip() {
        Address address = new Address.Builder()
                .setLine1(LINE1)
                .setLine2(LINE2)
                .setCity(CITY)
                .setState(STATE)
                .setCountry(COUNTRY)
                .setZip(BAD_ZIP_WRONG_LENGTH)
                .build();
        assertFalse(address.isValid());
    }

    @Test
    public void failedAddressCreation_WrongZip() {
        Address address = new Address.Builder()
                .setLine1(LINE1)
                .setLine2(LINE2)
                .setCity(CITY)
                .setState(STATE)
                .setCountry(COUNTRY)
                .setZip(BAD_ZIP_OUT_OF_RANGE)
                .build();
        assertFalse(address.isValid());
    }

    @Test
    public void successfulDistanceCalculation() {
        Address address1 = new Address.Builder()
                .setLine1(LINE1)
                .setLine2(LINE2)
                .setCity(CITY)
                .setState(STATE)
                .setCountry(COUNTRY)
                .setZip(ZIP1)
                .build();
        Address address2 = new Address.Builder()
                .setLine1(LINE1)
                .setLine2(LINE2)
                .setCity(CITY)
                .setState(STATE)
                .setCountry(COUNTRY)
                .setZip(ZIP2)
                .build();
        Double distance = address1.calculateDistanceTo(address2, "mi");
        assertFalse(distance == -1);
    }
}