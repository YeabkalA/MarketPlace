package com.example.yeabkalwubshit.marketplace.objects;

import java.util.HashMap;

/**
 * Interface for objects stored in the Firebase database.
 */
public interface DatabaseStorable {

    /**
     * Converts an object to a map for storing in database.
     * @return map representation of the object.
     */
    HashMap<String, Object> createMap();
}
