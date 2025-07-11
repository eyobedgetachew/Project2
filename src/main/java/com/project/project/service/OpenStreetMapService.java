package com.project.project.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class OpenStreetMapService {

    // You might want to move this URL to application.properties for easier configuration
    // Example: nominatim.api.url=https://nominatim.openstreetmap.org/search
    @Value("${nominatim.api.url:https://nominatim.openstreetmap.org/search}")
    private String nominatimApiUrl;

    private final RestTemplate restTemplate;

    public OpenStreetMapService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Geocodes an address using the Nominatim OpenStreetMap API.
     *
     * @param address The address string to geocode (e.g., "1600 Amphitheatre Parkway, Mountain View, CA").
     * @return A Map containing "latitude" and "longitude" as Double, or null if geocoding fails.
     */
    public Map<String, Double> getCoordinates(String address) {
        if (address == null || address.trim().isEmpty()) {
            return null;
        }

        // Build the URL with query parameters
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(nominatimApiUrl)
            .queryParam("format", "json") // Request JSON format
            .queryParam("q", address)     // The address query
            .queryParam("limit", 1);      // Limit to 1 result, most relevant one

        String url = builder.toUriString();

        try {
            // Nominatim returns a JSON array, even for a single result
            // We expect a List of Maps, where each Map represents a geocoded result
            List<Map<String, String>> results = restTemplate.getForObject(url, List.class);

            if (results != null && !results.isEmpty()) {
                Map<String, String> firstResult = results.get(0);
                // Nominatim returns 'lat' and 'lon' as strings, so parse them to Double
                Double lat = Double.parseDouble(firstResult.get("lat"));
                Double lon = Double.parseDouble(firstResult.get("lon"));

                return Map.of("latitude", lat, "longitude", lon);
            }
        } catch (Exception e) {
            // Log the exception for debugging
            System.err.println("Error geocoding address '" + address + "': " + e.getMessage());
            // Consider more robust error handling or custom exceptions
        }
        return null; // Return null if no coordinates found or an error occurred
    }
}