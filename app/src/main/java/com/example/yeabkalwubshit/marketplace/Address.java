package com.example.yeabkalwubshit.marketplace;

import java.util.HashMap;

public class Address implements DatabaseStorable{

    // Builder class for `Address`.
    static class Builder {
        private Address address;

        public Builder() {
            this.address = new Address();
        }

        public Builder setLine1(String line1) {
            this.address.setLine1(line1);
            return this;
        }
        public Builder setLine2(String line2) {
            this.address.setLine2(line2);
            return this;
        }
        public Builder setCity(String city) {
            this.address.setCity(city);
            return this;
        }
        public Builder setState(String state) {
            this.address.setState(state);
            return this;
        }
        public Builder setCountry(String country) {
            this.address.setCountry(country);
            return this;
        }
        public Builder setZip(String zip) {
            this.address.setZip(zip);
            return this;
        }
        public Address build() {
            return this.address;
        }
    }
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String country;
    private String zip;

    static private int LEAST_ZIPCODE = 501;
    static private int BIGGEST_ZIPCODE = 99950;

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public Address() {
        this.line1 = "";
        this.line2 = "";
        this.city = "";
        this.state = "";
        this.country = "";
        this.zip = "";
    }

    public static boolean isValidZipCode(String text) {
        String rep = text;
        rep = rep.replaceAll("[^0-9]", "");

        if(!rep.equals(text)) { return false; }

        boolean lenCheck = text.length() == 5;
        if(!lenCheck) {
            return false;
        }

        int numeric = Integer.parseInt(text);
        // Check if input is in the range of valid zip code values.
        if(numeric < LEAST_ZIPCODE || numeric > BIGGEST_ZIPCODE) {
            return false;
        }
        return true;
    }

    public boolean isValid() {
        // `line2` is optional, so it's not being checked.
        return isValidZipCode(this.zip)
                && this.line1.length() != 0
                && this.city.length() != 0
                && this.state.length() != 0
                && this.zip.length() != 0;
    }

    public Double calculateDistanceTo(Address second, String units) {
        if(!second.isValid()) {
            return -1.0;
        }
        Double distance;
        try {
            distance = AddressNetworkServices.getDistanceBetweenTwoZips(
                    this.zip, second.getZip(), units
            );
            return distance;
        } catch (Exception e) {
            // Failed
            return -1.0;
        }
    }

    @Override
    public HashMap<String, Object> createMap() {
        HashMap<String, Object> ret = new HashMap<>();
        ret.put("line1", line1);
        ret.put("line2", line2);
        ret.put("city", city);
        ret.put("state", state);
        ret.put("zip", zip);
        return ret;
    }
}
