package com.example.yeabkalwubshit.marketplace;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class EmailUtilTest {
    @Test
    public void correctFormat() {
        assertTrue(EmailUtil.validateEmail("test@email.com"));
    }

    @Test
    public void InCorrectFormat1() {
        assertFalse(EmailUtil.validateEmail("test@email@com"));
    }

    @Test
    public void InCorrectFormat2() {
        assertFalse(EmailUtil.validateEmail("test.email.com"));
    }
}
