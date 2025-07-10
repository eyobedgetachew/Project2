package com.project.project.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Added for consistency

import com.project.project.api.DTO.MenuItemDTO; // CHANGED: Import MenuItemDTO
import com.project.project.api.DTO.PlaceDTO;
import com.project.project.model.Place;
import com.project.project.model.dao.PlaceDAO;

@Service
public class FeedService {

    private final PlaceDAO placeDAO;

    public FeedService(PlaceDAO placeDAO) {
        this.placeDAO = placeDAO;
    }

    /**
     * Retrieves all places for the general feed, converting them to PlaceDTOs.
     * This method is transactional and read-only for performance.
     * @return A list of PlaceDTOs.
     */
    @Transactional(readOnly = true) // Added transactional annotation
    public List<PlaceDTO> getAllPlaces() {
        List<Place> places = placeDAO.findAll();
        return places.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Helper to convert a Place entity to a PlaceDTO.
     * Correctly maps all fields, including multiple menu items, media URLs, owner, and coordinates.
     * @param place The Place entity to convert.
     * @return A PlaceDTO representation.
     */
    private PlaceDTO toDTO(Place place) {
        PlaceDTO dto = new PlaceDTO();

        dto.setId(place.getId());
        dto.setName(place.getName());
        dto.setCuisine(place.getCuisine()); // NEW: Map cuisine
        dto.setOpeningHours(place.getOpeningHours());
        dto.setDescription(place.getDescription());
        dto.setAddress(place.getAddress());
        dto.setContactInfo(place.getContactInfo()); // NEW: Map contactInfo
        dto.setEmail(place.getEmail()); // NEW: Map email
        dto.setImageUrl(place.getImageUrl());
        dto.setVideoUrl(place.getVideoUrl());
        dto.setLatitude(place.getLatitude()); // NEW: Map latitude
        dto.setLongitude(place.getLongitude()); // NEW: Map longitude

        if (place.getOwner() != null) {
            dto.setOwnerUsername(place.getOwner().getUsername());
        } else {
            dto.setOwnerUsername(null); // Or some default
        }
        
        // CHANGED: Map List<Menu> to List<MenuItemDTO>
        if (place.getMenuItems() != null && !place.getMenuItems().isEmpty()) {
            List<MenuItemDTO> menuItemDTOs = place.getMenuItems().stream()
                .map(menuItem -> {
                    MenuItemDTO itemDTO = new MenuItemDTO();
                    // No ID or placeId needed in MenuItemDTO for display
                    itemDTO.setItem(menuItem.getItem());
                    itemDTO.setIngredients(menuItem.getIngredients());
                    itemDTO.setPrice(menuItem.getPrice());
                    return itemDTO;
                })
                .collect(Collectors.toList());
            dto.setMenuItems(menuItemDTOs); // Set the list of menu item DTOs
        } else {
            dto.setMenuItems(List.of()); // Ensure it's an empty list if no items
        }

        return dto;
    }
}
