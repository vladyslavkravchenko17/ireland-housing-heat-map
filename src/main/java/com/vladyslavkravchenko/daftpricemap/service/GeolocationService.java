package com.vladyslavkravchenko.daftpricemap.service;

import com.vladyslavkravchenko.daftpricemap.model.enums.County;
import jakarta.annotation.PostConstruct;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.json.JSONArray;
import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class GeolocationService {

    private static final String USER_AGENT = "DaftPriceMap/1.0";
    private static final String GEOJSON_PATH = "/static/data/ireland-counties.geojson";

    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final Map<County, Geometry> countyGeometries = new HashMap<>();
    private final RestTemplate restTemplate;

    public GeolocationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        try (InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(GEOJSON_PATH)))) {
            FeatureJSON featureJSON = new FeatureJSON();
            DefaultFeatureCollection featureCollection = (DefaultFeatureCollection) featureJSON.readFeatureCollection(reader);

            for (SimpleFeature feature : featureCollection) {
                String countyName = (String) feature.getAttribute("name");
                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                County county = County.fromDisplayName(countyName);
                countyGeometries.put(county, geometry);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load GeoJSON", e);
        }
    }

    public double[] getCoordinatesFromAddress(String address) {
        try {
            String query = URLEncoder.encode(address + ", Ireland", StandardCharsets.UTF_8);
            String url = "https://nominatim.openstreetmap.org/search?q=" + query + "&format=json&limit=1";

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", USER_AGENT);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            JSONArray json = new JSONArray(response.getBody());
            if (json.length() == 0) {
                throw new RuntimeException("Address not found: " + address);
            }

            JSONObject obj = json.getJSONObject(0);
            double lat = obj.getDouble("lat");
            double lon = obj.getDouble("lon");

            return new double[]{lat, lon};
        } catch (Exception e) {
            throw new RuntimeException("Failed to geocode address: " + address, e);
        }
    }

    public County detectCounty(double latitude, double longitude) {
        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        for (Map.Entry<County, Geometry> entry : countyGeometries.entrySet()) {
            if (entry.getValue().contains(point)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
