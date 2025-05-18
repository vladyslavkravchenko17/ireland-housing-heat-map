package com.vladyslavkravchenko.daftpricemap.util;

import com.vladyslavkravchenko.daftpricemap.model.enums.ListingDealType;
import com.vladyslavkravchenko.daftpricemap.model.PropertyListing;
import com.vladyslavkravchenko.daftpricemap.model.enums.PropertyType;
import com.vladyslavkravchenko.daftpricemap.service.ListingService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvListingLoader implements CommandLineRunner {

    private final ListingService listingService;

    public CsvListingLoader(ListingService listingService) {
        this.listingService = listingService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (listingService.count() == 0) {
            loadData();
        }
    }

    private void loadData() throws IOException {
        String filePath = Paths.get("src", "main", "resources", "listings.csv").toString();

        List<PropertyListing> listings = new ArrayList<>();

        try (CSVParser csvParser = new CSVParser(new FileReader(filePath), CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : csvParser) {
                PropertyListing listing = new PropertyListing(
                        record.get("address"),
                        ListingDealType.valueOf(record.get("dealType").toUpperCase()),
                        Integer.parseInt(record.get("price")),
                        PropertyType.fromString(record.get("propertyType")),
                        Integer.parseInt(record.get("size")),
                        Integer.parseInt(record.get("bedrooms"))
                );
                listings.add(listing);
            }
        }

        listingService.saveAll(listings);
        System.out.println("Synthetic data is loaded to the Database.");
    }
}
