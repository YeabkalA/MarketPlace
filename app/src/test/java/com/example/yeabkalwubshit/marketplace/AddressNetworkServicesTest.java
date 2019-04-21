package com.example.yeabkalwubshit.marketplace;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AddressNetworkServicesTest {
    @Test
    public void successfulDistanceCalculation() {
        double distance = -1;
        try {
            distance = AddressNetworkServices.getDistanceBetweenTwoZips("75024",
                    "75062", "km");
            System.out.println(distance);
        } catch (Exception e) {}
        assertTrue(distance >= 0);

        // Successful distance computations gives a non-negative number.

    }
}
