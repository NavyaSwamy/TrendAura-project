package com.trendaura.dto;

public class AddressResponse {

    private Long id;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String pincode;
    private String country;
    private String label;
    private boolean defaultAddress;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLine1() { return line1; }
    public void setLine1(String line1) { this.line1 = line1; }
    public String getLine2() { return line2; }
    public void setLine2(String line2) { this.line2 = line2; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public boolean isDefaultAddress() { return defaultAddress; }
    public void setDefaultAddress(boolean defaultAddress) { this.defaultAddress = defaultAddress; }
}
