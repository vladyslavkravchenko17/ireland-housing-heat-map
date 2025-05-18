package com.vladyslavkravchenko.daftpricemap.model.enums;

public enum ListingDealType {
    RENT("Rent"),
    SALE("Sale");

    private final String displayName;

    ListingDealType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ListingDealType fromString(String value) {
        try {
            return valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid listing deal type: " + value);
        }
    }
}