package com.example.yeabkalwubshit.marketplace.objects;

import com.example.yeabkalwubshit.marketplace.tools.EmailUtil;
import com.example.yeabkalwubshit.marketplace.tools.PhoneNumberUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents an app user.
 */
public class User implements DatabaseStorable {

    private String userName;
    private String firstName;
    private String lastName;
    private Address address;
    private String email;
    private String phoneNumber;
    private Rating rating;

    private List<Bid> bids;

    private String imageUrl;

    private String createdOn;

    public List<Bid> getBids() {
        return bids;
    }

    public void setBids(List<Bid> bids) {
        this.bids = bids;
    }

    public static class Builder {
        private User user;

        public Builder() { this.user = new User(); }

        public Builder setUserName(String userName) {
            this.user.setUserName(userName);
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.user.setFirstName(firstName);
            return this;
        }
        public Builder setLastName(String lastName) {
            this.user.setLastName(lastName);
            return this;
        }
        public Builder setAddress(Address address) {
            this.user.setAddress(address);
            return this;
        }
        public Builder setEmail(String email) {
            this.user.setEmail(email);
            return this;
        }
        public Builder setPhoneNumber(String phoneNumber) {
            this.user.setPhoneNumber(phoneNumber);
            return this;
        }

        public Builder setCreatedOn(String createdOn) {
            this.user.createdOn = createdOn;
            return this;
        }

        public Builder setImageURL(String imageURL) {
            this.user.imageUrl = imageURL;
            return this;
        }

        public User build() {
            this.user.rating = new Rating();
            return this.user;
        }

    }

    public boolean isValid() {
        return this.address.isValid()
                && EmailUtil.validateEmail(this.email)
                && PhoneNumberUtil.validPhoneNumber(this.phoneNumber);
    }

    public User() {
        this.bids = new ArrayList<>();
    }

    public void setUserName(String userName) { this.userName = userName; }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public void setCreatedOn(String createdOn) { this.createdOn = createdOn; }

    public String getUserName() {
        return userName;
    }

    public String getFirstName() {

        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Address getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getImageUrl() { return imageUrl; }

    public String getCreatedOn() { return createdOn; }

    public void setImageURL(String imageURL) { this.imageUrl = imageURL; }

    @Override
    public HashMap<String, Object> createMap() {
        System.out.println("Trying to make map");
        HashMap<String, Object> ret = new HashMap<>();
        ret.put("firstName", firstName);
        ret.put("lastName", lastName);
        ret.put("phoneNumber", phoneNumber);
        ret.put("email", email);
        ret.put("address", address.createMap());
        ret.put("createdOn", createdOn);
        ret.put("rating", rating.createMap());
        ret.put("imageUrl", imageUrl);
        ret.put("userName", userName);
        return ret;
    }

    public void populateFromMap(HashMap<String, Object> map) {
        String firstName = (String) map.get("firstName");
        String lastName = (String) map.get("lastName");
        String email = (String) map.get("email");
        String phone = (String) map.get("phoneNumber");
        String imageUrl = (String) map.get("imageUrl");

        HashMap<String, Object> addressInfo = (HashMap) map.get("address");
        Address address = new Address();
        address.populateFromMap(addressInfo);

        if(map.containsKey("bids")) {
            System.out.println("BIDS" + map.get("bids").toString());
            HashMap<String, Object> bids = (HashMap) map.get("bids");
            for(String bidId: bids.keySet()) {
                Bid bid = Bid.bidFromMap((HashMap) bids.get(bidId));
                this.bids.add(bid);
            }
        }

        if(map.containsKey("imageUrl")) {
            setImageURL(imageUrl);
        }

        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setPhoneNumber(phone);
        setAddress(address);
        setImageURL(imageUrl);
    }

}
