package com.example.yeabkalwubshit.marketplace;

import java.util.HashMap;

public class Bid implements DatabaseStorable {

    static class Builder {

        Bid bid;

        public Builder() {
            this.bid = new Bid();
        }

        public Builder setOwnerId(String id) {
            this.bid.setOwnerId(id);
            return this;
        }

        public Builder setIssuerId(String id) {
            this.bid.setIssuerId(id);
            return this;
        }

        public Builder setItemId(String id) {
            this.bid.setItemId(id);
            return this;
        }

        public Builder setValueInCents(Long val) {
            this.bid.setValueInCents(val);
            return this;
        }

        public Builder setId(Long val) {
            this.bid.setId(val);
            return this;
        }

        public Bid build() {
            return bid;
        }
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getIssuerId() {
        return issuerId;
    }

    public void setIssuerId(String issuerId) {
        this.issuerId = issuerId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Long getValueInCents() {
        return valueInCents;
    }

    public void setValueInCents(Long valueInCents) {
        this.valueInCents = valueInCents;
    }

    private String ownerId;
    private String issuerId;
    private String itemId;
    private Long valueInCents;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Long id;

    @Override
    public HashMap<String, Object> createMap() {
        HashMap<String, Object> ret = new HashMap<>();
        ret.put("ownerId", ownerId);
        ret.put("issuerId", issuerId);
        ret.put("itemId", itemId);
        ret.put("valueInCents", valueInCents);
        ret.put("id", id);
        return ret;
    }

    public static Bid bidFromMap(HashMap<String, Object> map) {
        if(map == null) return null;
        Bid bid = new Builder()
                .setOwnerId((String) map.get("ownerId"))
                .setIssuerId((String) map.get("issuerId"))
                .setItemId((String) map.get("itemId"))
                .setValueInCents((Long) map.get("valueInCents"))
                .setId((Long) map.get("id"))
                .build();
        return bid;
    }
}
