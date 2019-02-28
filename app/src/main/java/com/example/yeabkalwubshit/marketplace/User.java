package com.example.yeabkalwubshit.marketplace;

import java.util.HashMap;

public class User {
    private String firstName;
    private String lastName;
    private Address address;
    private String email;
    private String phoneNumber;

    private String imageUrl;

    static class Builder {
        private User user;

        public Builder() { this.user = new User(); }

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

        public User build() {
            return this.user;
        }

    }

    public boolean isValid() {
        return this.address.isValid()
                && EmailUtil.validateEmail(this.email)
                && PhoneNumberUtil.validPhoneNumber(this.phoneNumber);
    }

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

    public HashMap<String, Object> createMap() {
        System.out.println("Trying to make map");
        HashMap<String, Object> ret = new HashMap<>();
        ret.put("firstName", firstName);
        ret.put("lastName", lastName);
        ret.put("phoneNumber", phoneNumber);
        ret.put("email", email);
        ret.put("address", address.createMap());
        if(imageUrl != null && imageUrl.length() != 0) {
            ret.put("imageUrl", imageUrl);
        }
        return ret;
    }
}
