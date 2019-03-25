package com.example.yeabkalwubshit.marketplace;

import java.util.HashMap;

public class User {
    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    private String userName;
    private String firstName;
    private String lastName;
    private Address address;
    private String email;
    private String phoneNumber;
    private Rating rating;

    private String imageUrl;

    private String createdOn;

    static class Builder {
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
}
