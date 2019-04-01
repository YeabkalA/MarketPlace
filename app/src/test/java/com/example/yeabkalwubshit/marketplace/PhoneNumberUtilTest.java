package com.example.yeabkalwubshit.marketplace;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class PhoneNumberUtilTest {
    @Test
    public void correctFormat1() {
        assertTrue(PhoneNumberUtil.validPhoneNumber("1234567653"));
    }

    @Test
    public void correctFormat2() {
        assertTrue(PhoneNumberUtil.validPhoneNumber("123-456-7653"));
    }

    @Test
    public void badFormat_wrongLength1() {
        assertFalse(PhoneNumberUtil.validPhoneNumber("123457653"));
    }

    @Test
    public void badFormat_wrongLength2() {
        assertFalse(PhoneNumberUtil.validPhoneNumber("123-457-653"));
    }

    @Test
    public void badFormat_wrongFormat() {
        assertFalse(PhoneNumberUtil.validPhoneNumber("123-457653"));
    }

    @Test
    public void badFormat_notNumeric() {
        assertFalse(PhoneNumberUtil.validPhoneNumber("ssjlsdf"));
    }
}
