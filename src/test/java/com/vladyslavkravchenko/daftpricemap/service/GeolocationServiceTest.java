package com.vladyslavkravchenko.daftpricemap.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeolocationServiceTest {

    @InjectMocks
    private GeolocationService geoService;

    @Mock
    private RestTemplate restTemplate;

    @Test
    void testGetCoordinatesFromAddress_successfulResponse_returnsCoordinates() {
        String address = "Heytesbury Street";
        String jsonResponse = "[{\"lat\":\"53.3326768\",\"lon\":\"-6.2683522\"}]";

        ResponseEntity<String> response = new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                eq(String.class))
        ).thenReturn(response);

        double[] coords = geoService.getCoordinatesFromAddress(address);

        assertEquals(53.3326768, coords[0], 0.001);
        assertEquals(-6.2683522, coords[1], 0.001);
    }

    @Test
    void testGetCoordinatesFromAddress_notFound_throwsException() {
        String address = "Unknown Street";
        String jsonResponse = "[]";
        ResponseEntity<String> response = new ResponseEntity<>(jsonResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(response);

        assertThrows(RuntimeException.class, () ->
                geoService.getCoordinatesFromAddress(address));
    }
}
