package com.vladyslavkravchenko.daftpricemap.service;

import com.vladyslavkravchenko.daftpricemap.dto.CountyPriceDTO;
import com.vladyslavkravchenko.daftpricemap.model.enums.County;
import com.vladyslavkravchenko.daftpricemap.model.PropertyListing;
import com.vladyslavkravchenko.daftpricemap.model.enums.PropertyType;
import com.vladyslavkravchenko.daftpricemap.repository.ListingRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ListingService {
    private final ListingRepository listingRepository;
    private final GeolocationService geoCountyService;

    public ListingService(ListingRepository listingRepository, GeolocationService geoCountyService) {
        this.listingRepository = listingRepository;
        this.geoCountyService = geoCountyService;
    }

    public List<CountyPriceDTO> getMedianPricesFiltered(
            Optional<String> transactionType,
            Optional<List<String>> propertyTypes,
            Optional<Integer> sizeFrom,
            Optional<Integer> sizeTo,
            Optional<Integer> bedsFrom,
            Optional<Integer> bedsTo
    ) {
        List<PropertyListing> listings = listingRepository.findAll();

        Stream<PropertyListing> stream = listings.stream();

        if (transactionType.isPresent()) {
            String type = transactionType.get().toUpperCase();
            stream = stream.filter(l -> l.getDealType().name().equalsIgnoreCase(type));
        }

        if (propertyTypes.isPresent()) {
            List<PropertyType> types = propertyTypes.get().stream()
                    .map(PropertyType::fromDisplayName)
                    .flatMap(Optional::stream)
                    .toList();

            stream = stream.filter(l -> types.contains(l.getPropertyType()));
        }

        if (sizeFrom.isPresent()) {
            int from = sizeFrom.get();
            stream = stream.filter(l -> l.getSize() >= from);
        }

        if (sizeTo.isPresent()) {
            int to = sizeTo.get();
            stream = stream.filter(l -> l.getSize() <= to);
        }

        if (bedsFrom.isPresent()) {
            int from = bedsFrom.get();
            stream = stream.filter(l -> l.getBedrooms() >= from);
        }

        if (bedsTo.isPresent()) {
            int to = bedsTo.get();
            stream = stream.filter(l -> l.getBedrooms() <= to);
        }

        List<PropertyListing> filtered = stream.toList();

        Map<County, List<Integer>> pricesByCounty = filtered.stream()
                .collect(Collectors.groupingBy(
                        PropertyListing::getCounty,
                        Collectors.mapping(PropertyListing::getPrice, Collectors.toList())
                ));

        return pricesByCounty.entrySet().stream()
                .map(entry -> {
                    County county = entry.getKey();
                    List<Integer> prices = entry.getValue();
                    double median = calculateMedian(prices);
                    return new CountyPriceDTO(county.getDisplayName(), median);
                })
                .toList();
    }

    private double calculateMedian(List<Integer> prices) {
        List<Integer> sorted = new ArrayList<>(prices);
        Collections.sort(sorted);
        int n = sorted.size();
        if (n == 0) return 0;
        if (n % 2 == 1) return sorted.get(n / 2);
        return (sorted.get(n / 2 - 1) + sorted.get(n / 2)) / 2.0;
    }

    public void saveAll(List<PropertyListing> listings) {
        int i = 2;
        for (PropertyListing listing : listings) {
            double[] coords = geoCountyService.getCoordinatesFromAddress(listing.getAddress());
            double latitude = coords[0];
            double longitude = coords[1];
            listing.setLatitude(latitude);
            listing.setLongitude(longitude);
            System.out.println(latitude + " " + longitude);
            County county = geoCountyService.detectCounty(latitude, longitude);
            System.out.println(i + " " + county);
            i++;
            listing.setCounty(county);
        }
        listingRepository.saveAll(listings);
    }

    public long count() {
        return listingRepository.count();
    }
}
