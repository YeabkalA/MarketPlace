package com.example.yeabkalwubshit.marketplace;

import java.util.HashMap;

public class Rating implements DatabaseStorable{
    private long value;
    private long count;

    public Rating() {
        this.value = 0;
        this.count = 0;
    }

    public long getValue() { return value; }

    public void update(int newRating) {
        value = Math.round(1.0 * (((value * count) + newRating))/(count + 1));
        count++;
    }

    public String getStringRep() {
        return String.format("%.2f", value);
    }

    @Override
    public HashMap<String, Object> createMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("value", value);
        map.put("count", count);

        return map;
    }

    public void populateFromMap(HashMap<String, Object> map) {
        this.value = (long) map.get("value");
        this.count = (long) map.get("count");
    }

    public static Rating updateMapRatingWithNewRating(HashMap<String, Object> map, int newRating) {
        Rating rating = new Rating();
        rating.populateFromMap(map);
        rating.update(newRating);
        return rating;
    }
}
