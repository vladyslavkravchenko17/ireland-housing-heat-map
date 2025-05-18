package com.vladyslavkravchenko.daftpricemap.model;

import com.vladyslavkravchenko.daftpricemap.model.enums.County;
import com.vladyslavkravchenko.daftpricemap.model.enums.PropertyType;
import com.vladyslavkravchenko.daftpricemap.model.enums.ListingDealType;
import jakarta.persistence.*;


@Entity
@Table(name = "listing")

public class PropertyListing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private County county;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingDealType dealType;

    @Column(nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType propertyType;

    @Column(nullable = false)
    private int size;

    @Column(nullable = false)
    private int bedrooms;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;


    public PropertyListing(String address, ListingDealType dealType, int price, PropertyType propertyType, int size, int bedrooms) {
        this.address = address;
        this.dealType = dealType;
        this.price = price;
        this.propertyType = propertyType;
        this.size = size;
        this.bedrooms = bedrooms;
    }

    public PropertyListing() {
    }

    public ListingDealType getDealType() {
        return dealType;
    }

    public String getAddress() {
        return address;
    }

    public County getCounty() {
        return county;
    }

    public int getPrice() {
        return price;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public int getSize() {
        return size;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCounty(County county) {
        this.county = county;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}