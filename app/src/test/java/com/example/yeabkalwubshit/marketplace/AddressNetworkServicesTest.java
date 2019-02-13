package com.example.yeabkalwubshit.marketplace;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AddressNetworkServicesTest {
    @Test
    public void successfulDistanceCalculation() {
        Double distance = AddressNetworkServices.getDistanceBetweenTwoZips("75024",
                "75062", "km");
        // Successful distance computations gives a non-negative number.
        assertTrue(distance >= 0);
    }
}
