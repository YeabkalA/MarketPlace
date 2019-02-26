package com.example.yeabkalwubshit.marketplace;

public class Item {
    private String id;
    private String description;
    private String ownerId;
    private String imageURL;
    private String title;
    private Long priceInCents;
    private String ownerZip;

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

    public static String getDollarRepresentation(Long cents) {
        return Double.toString(cents/(100.0));
    }

}
