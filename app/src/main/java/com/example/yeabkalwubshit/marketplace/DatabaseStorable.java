package com.example.yeabkalwubshit.marketplace;

import java.util.HashMap;

public interface DatabaseStorable {

    /**
     * Converts an object to a map for storing in database.
     * @return map representation of the object.
     */
    HashMap<String, Object> createMap();
}
