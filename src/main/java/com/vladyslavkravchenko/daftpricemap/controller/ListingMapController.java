package com.vladyslavkravchenko.daftpricemap.controller;

import com.vladyslavkravchenko.daftpricemap.dto.CountyPriceDTO;
import com.vladyslavkravchenko.daftpricemap.service.ListingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@Controller
public class ListingMapController {

    private final ListingService listingService;

    public ListingMapController(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping("/")
    public String index() {
        return "housing_heatmap";
    }

    @GetMapping("/api/prices")
    @ResponseBody
    public List<CountyPriceDTO> getFilteredPrices(
            @RequestParam Optional<String> transactionType,
            @RequestParam Optional<List<String>> propertyTypes,
            @RequestParam Optional<Integer> sizeFrom,
            @RequestParam Optional<Integer> sizeTo,
            @RequestParam Optional<Integer> bedsFrom,
            @RequestParam Optional<Integer> bedsTo
    ) {

        return listingService.getMedianPricesFiltered(
                transactionType,
                propertyTypes,
                sizeFrom,
                sizeTo,
                bedsFrom,
                bedsTo
        );
    }
}
