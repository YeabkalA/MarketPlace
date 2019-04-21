package com.example.yeabkalwubshit.marketplace;

import java.util.HashMap;

public class ItemStatus implements DatabaseStorable{
    public final static String STATUS_AVAILABLE = "AVAILABLE";
    public final static String STATUS_FINALIZING = "FINALIZING";
    public final static String STATUS_COMPLETED = "COMPLETED";

    public ItemStatus(String status, String bidWinnerId) {
        this.status = status;
        this.bidWinnerId = bidWinnerId;
    }

    public ItemStatus() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBidWinnerId() {
        return bidWinnerId;
    }

    public void setBidWinnerId(String bidWinnerId) {
        this.bidWinnerId = bidWinnerId;
    }

    String status;
    String bidWinnerId;

    @Override
    public HashMap<String, Object> createMap() {
        HashMap<String, Object> ret = new HashMap<>();
        ret.put("status", status);
        ret.put("bidWinnerId", bidWinnerId);
        return ret;
    }

    public void populateFromMap(HashMap<String, Object> map) {
        setStatus((String) map.get("status"));
        setBidWinnerId((String) map.get("bidWinnerId"));
    }

}
