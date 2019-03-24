package com.example.yeabkalwubshit.marketplace;

import java.util.HashMap;

public class Item {

    private String id;
    private String description;
    private String condition;
    private String ownerId;
    private String imageURL;
    private String title;
    private Long priceInCents;
    private String ownerZip;
    private String postedOn;

    // Builder for item.
    static class Builder {
        private Item item;

        public Builder() { this.item = new Item(); }

        public Builder setId(String id) {
            this.item.id = id;
            return this;
        }

        public Builder setDescription(String description) {
            this.item.description = description;
            return this;
        }

        public Builder setCondition(String condition) {
            this.item.condition = condition;
            return this;
        }

        public Builder setOwnerId(String ownerId) {
            this.item.ownerId = ownerId;
            return this;
        }

        public Builder setTitle(String title) {
            this.item.title = title;
            return this;
        }

        public Builder setPriceInCents(Long priceInCents) {
            this.item.priceInCents = priceInCents;
            return this;
        }

        public Builder setOwnerZip(String ownerZip) {
            this.item.ownerZip = ownerZip;
            return this;
        }

        public Builder setPostedOn(String postedOnDate) {
            this.item.postedOn = postedOnDate;
            return this;
        }

        public Item build() {
            return this.item;
        }

    }

    public Item() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getPriceInCents() {
        return priceInCents;
    }

    public void setPriceInCents(Long priceInCents) {
        this.priceInCents = priceInCents;
    }

    public String getOwnerZip() {
        return ownerZip;
    }

    public void setOwnerZip(String ownerZip) {
        this.ownerZip = ownerZip;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(String postedOn) {
        this.postedOn = postedOn;
    }

    public static String getDollarRepresentation(Long cents) {
        return Double.toString(cents/(100.0));
    }

    public boolean isValidCondition() {
        return this.condition.equals("NEW") || this.condition.equals("USED");
    }

    public HashMap<String, Object> createMap() {
        HashMap<String, Object> ret = new HashMap<>();
        if(id != null) {
            ret.put("id", id);
        }
        if(title != null) {
            ret.put("title", title);
        }
        if(description != null) {
            ret.put("description", description);
        }
        if(priceInCents != null) {
            ret.put("priceInCents", priceInCents);
        }
        if(ownerId != null) {
            ret.put("owner_id", ownerId);
        }
        if(ownerZip != null) {
            ret.put("owner_zip", ownerZip);
        }
        if(imageURL != null) {
            ret.put("imageURL", imageURL);
        }
        if(condition != null) {
            ret.put("condition", condition);
        }
        if(postedOn != null) {
            ret.put("postedOn", postedOn);
        }
        return ret;
    }

    public boolean populateFromMap(HashMap<String, Object> map) {
        String title = (String) map.get("title");
        String desc = (String) map.get("description");
        Long price = (Long) map.get("priceInCents");
        String ownerId = (String) map.get("owner_id");
        String ownerZip = "";
        if(map.containsKey("owner_zip")) {
            ownerZip = (String) map.get("owner_zip");
        }

        if(map.containsKey("imageURL")) {
            setImageURL((String) map.get("imageURL"));
        }

        setTitle(title);
        setDescription(desc);
        setPriceInCents(price);
        setOwnerId(ownerId);
        setOwnerZip(ownerZip);

        return true;
    }

}
