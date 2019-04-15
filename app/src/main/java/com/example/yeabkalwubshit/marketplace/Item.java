package com.example.yeabkalwubshit.marketplace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Item implements DatabaseStorable {

    private String id;
    private String description;
    private String condition;
    private String ownerId;
    private String imageURL;
    private String title;
    private Long priceInCents;
    private String ownerZip;
    private String postedOn;
    private Bid winningBid;

    public String getOwnerUserName() {
        return ownerUserName;
    }

    public void setOwnerUserName(String ownerUserName) {
        this.ownerUserName = ownerUserName;
    }

    private String ownerUserName;

    public List<Bid> getBids() {
        return bids;
    }

    public void setBids(List<Bid> bids) {
        this.bids = bids;
    }

    public Bid getWinningBid() {
        return winningBid;
    }

    public Bid calculateWinningBid() {
        if(bids.size() == 0) return null;
        long maxVal = Long.MIN_VALUE;
        for(Bid bid: bids) {
            if(bid.getValueInCents() > maxVal) {
                maxVal = bid.getValueInCents();
                winningBid = bid;
            }
        }
        return winningBid;
    }


    private List<Bid> bids;

    private String category;

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

        public Builder setCategory(String category) {
            this.item.category = category;
            return this;
        }

        public Builder setOwnerUserName(String ownerUserName) {
            this.item.ownerUserName = ownerUserName;
            return this;
        }

        public Item build() {
            return this.item;
        }

    }

    public Item() {
        bids = new ArrayList<>();
    }

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public static String getDollarRepresentation(Long cents) {
        return Double.toString(cents/(100.0));
    }

    public boolean isValidCondition() {
        return this.condition.equals("NEW") || this.condition.equals("USED");
    }

    @Override
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

        ret.put("category", category);
        ret.put("ownerUserName", ownerUserName);

        return ret;
    }

    public boolean populateFromMap(HashMap<String, Object> map) {
        System.out.println("The map \n" + map);
        String id = (String) map.get("id");
        String title = (String) map.get("title");
        String desc = (String) map.get("description");
        Long price = (Long) map.get("priceInCents");
        String condition = (String) map.get("condition");
        String ownerId = (String) map.get("owner_id");
        String postedOn = (String) map.get("postedOn");
        String ownerZip = (String) map.get("owner_zip");
        String imageURL = (String) map.get("imageURL");
        String category = (String) map.get("category");
        String ownerUserName = (String) map.get("ownerUserName");

        if(map.containsKey("bids")) {
            System.out.println("BIDS" + map.get("bids").toString());
            HashMap<String, Object> bids = (HashMap) map.get("bids");
            for(String bidId: bids.keySet()) {
                Bid bid = Bid.bidFromMap((HashMap) bids.get(bidId));
                this.bids.add(bid);
            }
        }

        setId(id);
        setTitle(title);
        setDescription(desc);
        setPriceInCents(price);
        setCondition(condition);
        setOwnerId(ownerId);
        setPostedOn(postedOn);
        setOwnerZip(ownerZip);
        setImageURL(imageURL);
        setCategory(category);
        setOwnerUserName(ownerUserName);
        return true;
    }

}
