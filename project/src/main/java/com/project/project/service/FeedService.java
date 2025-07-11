package com.project.project.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.project.project.api.DTO.MenuDTO; // Import MenuDTO
import com.project.project.api.DTO.PlaceDTO;
import com.project.project.model.Menu;
import com.project.project.model.Place;
import com.project.project.model.dao.PlaceDAO;

@Service
public class FeedService {

    private final PlaceDAO placeDAO;

    public FeedService(PlaceDAO placeDAO) {
        this.placeDAO = placeDAO;
    }

    // For users: get all places with their menus (each menu list has 0 or 1 MenuDTO)
    public List<PlaceDTO> getAllPlaces() {
        List<Place> places = placeDAO.findAll();
        return places.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * UPDATED METHOD: Helper to convert a Place entity to a PlaceDTO.
     * Correctly maps all fields, including the single menu and new media URLs/owner.
     */
    private PlaceDTO toDTO(Place place) {
        PlaceDTO dto = new PlaceDTO(); // Create new PlaceDTO object

        dto.setId(place.getId());
        dto.setName(place.getName());
        dto.setOpeningHours(place.getOpeningHours()); // Map openingHours
        dto.setDescription(place.getDescription()); // Map description
        // Ensure owner is loaded or handle potential lazy loading issues if not EAGER
        if (place.getOwner() != null) {
            dto.setOwnerUsername(place.getOwner().getUsername()); // Map owner's username
        } else {
            dto.setOwnerUsername(null); // Or some default
        }
        
        dto.setImageUrl(place.getImageUrl()); // Map imageUrl
        dto.setVideoUrl(place.getVideoUrl()); // Map videoUrl

        // Map single Menu entity to MenuDTO (NOT a List)
        if (place.getMenu() != null) {
            Menu menu = place.getMenu();
            MenuDTO menuDTO = new MenuDTO(
                menu.getId(),
                place.getId(), // Include placeId in MenuDTO
                menu.getItem(),
                menu.getIngredients(),
                menu.getPrice()
            );
            dto.setMenu(menuDTO); // Set the single MenuDTO
        } else {
            dto.setMenu(null); // Explicitly set to null if no menu
        }

        return dto;
    }

    // REMOVED: public PlaceDTO createPlaceWithMenu(PlaceDTO placeDto, MenuDTO menuDto)
    // This functionality is now handled by PlaceService.createPlace and PlaceController.
}