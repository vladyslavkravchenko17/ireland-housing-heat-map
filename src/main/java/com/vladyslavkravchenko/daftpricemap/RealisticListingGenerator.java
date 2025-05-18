package com.vladyslavkravchenko.daftpricemap;

import com.vladyslavkravchenko.daftpricemap.model.enums.PropertyType;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RealisticListingGenerator {

    private static final String[] COUNTIES = {
            "Cork County", "Dun Laoghaire", "Cork", "Kerry County", "Limerick City", "Limerick County",
            "Tipperary", "Kilkeary", "Tycor", "Waterford County", "Kilkenny County", "Wexford County",
            "Carlow County", "Clare County", "Laois County", "Kildare County", "Wicklow County",
            "Galway City", "Tuam", "Leitrim County", "Roscommon", "Mullingar", "Offaly County",
            "Longford County", "Meath County", "Kimmage", "South Dublin", "Fingal", "Mayo County",
            "Sligo County", "Cavan County", "Monaghan County", "Louth County", "Donegal County"
    };

    private static final String[] DEAL_TYPES = {"rent", "sale"};

    private static final PropertyType[] PROPERTY_TYPES = PropertyType.values();

    private static final Random RANDOM = new Random();

    private static final Map<String, Integer> BASE_PRICE_MAP = new HashMap<>();

    static {
        BASE_PRICE_MAP.put("Dublin", 3500);
        BASE_PRICE_MAP.put("South Dublin", 3200);
        BASE_PRICE_MAP.put("Fingal", 3100);
        BASE_PRICE_MAP.put("Dun Laoghaire", 3300);

        BASE_PRICE_MAP.put("Cork", 1800);
        BASE_PRICE_MAP.put("Cork County", 1400);

        BASE_PRICE_MAP.put("Limerick City", 1400);
        BASE_PRICE_MAP.put("Limerick County", 1100);

        BASE_PRICE_MAP.put("Galway City", 1500);
        BASE_PRICE_MAP.put("Tuam", 1100);

        BASE_PRICE_MAP.put("Kerry County", 900);
        BASE_PRICE_MAP.put("Tipperary", 950);
        BASE_PRICE_MAP.put("Kilkeary", 900);
        BASE_PRICE_MAP.put("Tycor", 900);

        BASE_PRICE_MAP.put("Waterford County", 850);
        BASE_PRICE_MAP.put("Kilkenny County", 850);
        BASE_PRICE_MAP.put("Wexford County", 900);
        BASE_PRICE_MAP.put("Carlow County", 800);
        BASE_PRICE_MAP.put("Clare County", 950);
        BASE_PRICE_MAP.put("Laois County", 800);
        BASE_PRICE_MAP.put("Kildare County", 1100);
        BASE_PRICE_MAP.put("Wicklow County", 1300);
        BASE_PRICE_MAP.put("Leitrim County", 700);
        BASE_PRICE_MAP.put("Roscommon", 700);
        BASE_PRICE_MAP.put("Mullingar", 750);
        BASE_PRICE_MAP.put("Offaly County", 750);
        BASE_PRICE_MAP.put("Longford County", 700);
        BASE_PRICE_MAP.put("Meath County", 1000);
        BASE_PRICE_MAP.put("Kimmage", 1000);
        BASE_PRICE_MAP.put("Mayo County", 700);
        BASE_PRICE_MAP.put("Sligo County", 700);
        BASE_PRICE_MAP.put("Cavan County", 700);
        BASE_PRICE_MAP.put("Monaghan County", 700);
        BASE_PRICE_MAP.put("Louth County", 950);
        BASE_PRICE_MAP.put("Donegal County", 650);
    }

    public static void main(String[] args) {
        String filePath = "src/main/resources/listings.csv";

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("address,dealType,price,propertyType,bedrooms,size\n");

            for (int i = 0; i < 1000; i++) {
                String county = randomCounty();
                String dealType = randomDealType();
                PropertyType propertyType = randomPropertyType();
                int bedrooms = randomBedrooms(propertyType);
                int size = randomSize(propertyType, bedrooms);
                int price = calculatePrice(dealType, propertyType, bedrooms, size, county);

                writer.write(String.format("%s,%s,%d,%s,%d,%d\n",
                        county, dealType, price, propertyType.getDisplayName(), bedrooms, size));
            }

            System.out.println("Файл успешно создан: " + filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при записи файла: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private static String randomCounty() {
        return COUNTIES[RANDOM.nextInt(COUNTIES.length)];
    }

    private static String randomDealType() {
        return DEAL_TYPES[RANDOM.nextInt(DEAL_TYPES.length)];
    }

    private static PropertyType randomPropertyType() {
        return PROPERTY_TYPES[RANDOM.nextInt(PROPERTY_TYPES.length)];
    }

    private static int randomBedrooms(PropertyType type) {
        return switch (type) {
            case STUDIO_APARTMENT, SITE -> 0;
            case DUPLEX, APARTMENT, TOWNHOUSE, TERRACED_HOUSE, END_OF_TERRACE_HOUSE, SEMI_DETACHED_HOUSE, DETACHED_HOUSE, HOUSE, BUNGALOW ->
                    RANDOM.nextInt(12) + 1;
        };
    }

    private static int randomSize(PropertyType type, int bedrooms) {
        return switch (type) {
            case STUDIO_APARTMENT -> 30 + RANDOM.nextInt(20);
            case SITE -> 500 + RANDOM.nextInt(1000);
            case DUPLEX -> 80 + bedrooms * 20 + RANDOM.nextInt(20);
            case APARTMENT, TOWNHOUSE, TERRACED_HOUSE, END_OF_TERRACE_HOUSE -> 60 + bedrooms * 25 + RANDOM.nextInt(30);
            case SEMI_DETACHED_HOUSE, DETACHED_HOUSE, HOUSE, BUNGALOW -> 80 + bedrooms * 30 + RANDOM.nextInt(50);
        };
    }

    private static int calculatePrice(String dealType, PropertyType type, int bedrooms, int size, String county) {
        int basePricePerSqM = BASE_PRICE_MAP.getOrDefault(county, 800);

        double typeMultiplier = switch (type) {
            case STUDIO_APARTMENT -> 0.7;
            case SITE -> 0.3;
            case DUPLEX, APARTMENT -> 1.1;
            case TOWNHOUSE, TERRACED_HOUSE, END_OF_TERRACE_HOUSE -> 1.2;
            case SEMI_DETACHED_HOUSE -> 1.4;
            case DETACHED_HOUSE, HOUSE -> 1.6;
            case BUNGALOW -> 1.5;
        };

        double bedroomsMultiplier = 1 + 0.15 * bedrooms;
        double dealMultiplier = dealType.equals("rent") ? 0.004 : 1.0;

        double price = size * basePricePerSqM * typeMultiplier * bedroomsMultiplier * dealMultiplier;

        return (int) Math.round(price);
    }
}