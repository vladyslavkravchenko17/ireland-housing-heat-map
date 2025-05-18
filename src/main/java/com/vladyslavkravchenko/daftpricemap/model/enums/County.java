package com.vladyslavkravchenko.daftpricemap.model.enums;

public enum County {
    CORK_COUNTY("Cork County"),
    CORK_CITY("Cork City"),
    KERRY_COUNTY("Kerry County"),
    LIMERICK_CITY("Limerick City"),
    LIMERICK_COUNTY("Limerick County"),
    SOUTH_TIPPERARY("South Tipperary"),
    NORTH_TIPPERARY("North Tipperary"),
    WATERFORD_CITY("Waterford City"),
    WATERFORD_COUNTY("Waterford County"),
    KILKENNY_COUNTY("Kilkenny County"),
    WEXFORD_COUNTY("Wexford County"),
    CARLOW_COUNTY("Carlow County"),
    CLARE_COUNTY("Clare County"),
    LAOIS_COUNTY("Laois County"),
    KILDARE_COUNTY("Kildare County"),
    WICKLOW_COUNTY("Wicklow County"),
    GALWAY_CITY("Galway City"),
    GALWAY_COUNTY("Galway County"),
    LEITRIM_COUNTY("Leitrim County"),
    ROSCOMMON_COUNTY("Roscommon County"),
    WESTMEATH_COUNTY("Westmeath County"),
    OFFALY_COUNTY("Offaly County"),
    LONGFORD_COUNTY("Longford County"),
    MEATH_COUNTY("Meath County"),
    LAOGHAIRE_RATHDOWN("Dún Laoghaire-Rathdown"),
    DUBLIN_CITY("Dublin City"),
    SOUTH_DUBLIN("South Dublin"),
    FINGAL("Fingal"),
    MAYO_COUNTY("Mayo County"),
    SLIGO_COUNTY("Sligo County"),
    CAVAN_COUNTY("Cavan County"),
    MONAGHAN_COUNTY("Monaghan County"),
    LOUTH_COUNTY("Louth County"),
    DONEGAL_COUNTY("Donegal County");
    private final String displayName;

    County(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static County fromDisplayName(String name) {
        for (County county : values()) {
            if (county.displayName.equalsIgnoreCase(name) || county.name().replace("_", " ").equalsIgnoreCase(name)) {
                return county;
            }
        }
        throw new IllegalArgumentException("Unknown county name: " + name);
    }
}
