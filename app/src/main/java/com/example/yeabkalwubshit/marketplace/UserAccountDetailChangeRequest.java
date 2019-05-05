package com.example.yeabkalwubshit.marketplace;

import android.text.TextUtils;
import android.widget.TextView;

public class UserAccountDetailChangeRequest {

    public String getFirstName() {
        return firstName;
    }

    public UserAccountDetailChangeRequest setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public UserAccountDetailChangeRequest setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getCity() {
        return city;
    }

    public UserAccountDetailChangeRequest setCity(String city) {
        this.city = city;
        return this;
    }

    public String getZipCode() {
        return zipCode;
    }

    public UserAccountDetailChangeRequest setZipCode(String zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public UserAccountDetailChangeRequest setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getState() {
        return state;
    }

    public UserAccountDetailChangeRequest setState(String state) {
        this.state = state;
        return this;
    }

    public Error isValidRequest() {
        if(TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName)) {
            return new Error("Enter both first and last name");
        }
        if(!PhoneNumberUtil.validPhoneNumber(phoneNumber)) {
            return new Error("Enter a valid phone number xxx-xxx-xxxx or 10 digits");
        }
        if(TextUtils.isEmpty(city)) {
            return new Error("Enter city");
        }
        if(!Address.isValidZipCode(zipCode)) {
            return new Error("Enter a valid zip code");
        }
        return null;
    }

    String firstName;
    String lastName;
    String city;
    String zipCode;
    String phoneNumber;
    String state;

}
