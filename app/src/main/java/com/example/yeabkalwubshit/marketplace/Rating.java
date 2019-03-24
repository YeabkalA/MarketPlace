package com.example.yeabkalwubshit.marketplace;

import java.util.HashMap;

public class Rating {
    private double value;
    private int count;

    public Rating() {
        this.value = 0;
        this.count = 0;
    }

    public void update(int newRating) {
        value = ((value * count) + newRating)/(count + 1);
    }

    public String getStringRep() {
        return String.format("%.2f", value);
    }

    public HashMap<String, Object> createMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("value", value);
        map.put("count", count);

        return map;
    }

    public void populateFromMap(HashMap<String, Object> map) {
        this.value = (double) map.get("value");
        this.count = (int) map.get("count");
    }

    public static Rating updateMapRatingWithNewRating(HashMap<String, Object> map, int newRating) {
        Rating rating = new Rating();
        rating.populateFromMap(map);
        rating.update(newRating);
        return rating;

    }
}
