package com.vladyslavkravchenko.daftpricemap.service;

import com.vladyslavkravchenko.daftpricemap.dto.CountyPriceDTO;
import com.vladyslavkravchenko.daftpricemap.model.enums.County;
import com.vladyslavkravchenko.daftpricemap.model.enums.PropertyType;
import com.vladyslavkravchenko.daftpricemap.model.PropertyListing;
import com.vladyslavkravchenko.daftpricemap.repository.ListingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ListingServiceTest {

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private GeolocationService geoCountyService;

    @InjectMocks
    private ListingService listingService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMedianPricesFiltered_withNoFilters_returnsCorrectMedian() {
        PropertyListing p1 = new PropertyListing();
        p1.setCounty(County.DUBLIN_CITY);
        p1.setPrice(1000);
        p1.setPropertyType(PropertyType.APARTMENT);
        p1.setSize(50);
        p1.setBedrooms(2);

        PropertyListing p2 = new PropertyListing();
        p2.setCounty(County.DUBLIN_CITY);
        p2.setPrice(3000);
        p2.setPropertyType(PropertyType.APARTMENT);
        p2.setSize(80);
        p2.setBedrooms(3);

        when(listingRepository.findAll()).thenReturn(List.of(p1, p2));

        List<CountyPriceDTO> result = listingService.getMedianPricesFiltered(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty()
        );

        assertEquals(1, result.size());
        assertEquals("Dublin City", result.get(0).county());
        assertEquals(2000, result.get(0).medianPrice());
    }

    @Test
    void testGetMedianPricesFiltered_withFilters_appliesCorrectly() {
        PropertyListing p1 = new PropertyListing();
        p1.setCounty(County.DUBLIN_CITY);
        p1.setPrice(1500);
        p1.setPropertyType(PropertyType.APARTMENT);
        p1.setSize(55);
        p1.setBedrooms(2);

        PropertyListing p2 = new PropertyListing();
        p2.setCounty(County.DUBLIN_CITY);
        p2.setPrice(2500);
        p2.setPropertyType(PropertyType.HOUSE);
        p2.setSize(90);
        p2.setBedrooms(4);

        when(listingRepository.findAll()).thenReturn(List.of(p1, p2));

        List<CountyPriceDTO> result = listingService.getMedianPricesFiltered(
                Optional.empty(),
                Optional.of(List.of("Apartment")),
                Optional.of(50), Optional.of(60),
                Optional.of(1), Optional.of(3)
        );

        assertEquals(1, result.size());
        assertEquals(1500, result.get(0).medianPrice());
    }

    @Test
    void testSaveAll_setsCoordinatesAndCounty() {
        PropertyListing listing = new PropertyListing();
        listing.setAddress("Heytesbury Street");

        List<PropertyListing> listings = List.of(listing);

        when(geoCountyService.getCoordinatesFromAddress(anyString()))
                .thenReturn(new double[]{53.3, -6.26});
        when(geoCountyService.detectCounty(53.3, -6.26))
                .thenReturn(County.DUBLIN_CITY);

        listingService.saveAll(listings);

        assertEquals(53.3, listing.getLatitude());
        assertEquals(-6.26, listing.getLongitude());
        assertEquals(County.DUBLIN_CITY, listing.getCounty());

        verify(listingRepository, times(1)).saveAll(listings);
    }

    @Test
    void testCount_delegatesToRepository() {
        when(listingRepository.count()).thenReturn(42L);
        assertEquals(42L, listingService.count());
    }
}
