package com.meridian.commerce.order;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ShippingAddress {

    @Column(name = "ship_name")
    private String name;
    @Column(name = "ship_line1")
    private String line1;
    @Column(name = "ship_line2")
    private String line2;
    @Column(name = "ship_city")
    private String city;
    @Column(name = "ship_state")
    private String state;
    @Column(name = "ship_postal_code")
    private String postalCode;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLine1() { return line1; }
    public void setLine1(String line1) { this.line1 = line1; }
    public String getLine2() { return line2; }
    public void setLine2(String line2) { this.line2 = line2; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
}
