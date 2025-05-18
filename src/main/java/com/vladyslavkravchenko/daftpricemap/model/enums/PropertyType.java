package com.vladyslavkravchenko.daftpricemap.model.enums;

import java.util.Arrays;
import java.util.Optional;

public enum PropertyType {
    HOUSE("House"),
    DETACHED_HOUSE("Detached House"),
    SEMI_DETACHED_HOUSE("Semi Detached House"),
    TERRACED_HOUSE("Terraced House"),
    END_OF_TERRACE_HOUSE("End of Terrace House"),
    TOWNHOUSE("Townhouse"),
    APARTMENT("Apartment"),
    STUDIO_APARTMENT("Studio Apartment"),
    DUPLEX("Duplex"),
    BUNGALOW("Bungalow"),
    SITE("Site");

    private final String displayName;

    PropertyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PropertyType fromString(String value) {
        return fromDisplayName(value)
                .orElseThrow(() -> new IllegalArgumentException("Unknown property type: " + value));
    }

    public static Optional<PropertyType> fromDisplayName(String name) {
        return Arrays.stream(values())
                .filter(e -> e.displayName.equalsIgnoreCase(name) || e.name().equalsIgnoreCase(name))
                .findFirst();
    }
}
