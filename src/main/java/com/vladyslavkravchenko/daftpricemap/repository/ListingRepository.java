package com.vladyslavkravchenko.daftpricemap.repository;

import com.vladyslavkravchenko.daftpricemap.model.PropertyListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListingRepository extends JpaRepository<PropertyListing, Long> {
}