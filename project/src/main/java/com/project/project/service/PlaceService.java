package com.project.project.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.project.api.DTO.MenuDTO;
import com.project.project.api.DTO.PlaceCreateDTO;
import com.project.project.api.DTO.PlaceDTO;
import com.project.project.model.Menu;
import com.project.project.model.MyUser;
import com.project.project.model.Place;
import com.project.project.model.dao.MenuDAO;
import com.project.project.model.dao.MyUserDAO;
import com.project.project.model.dao.PlaceDAO;

@Service
public class PlaceService {

    private final PlaceDAO placeDAO;
    private final MyUserDAO myUserDAO;
    private final CloudinaryService cloudinaryService;
    private final MenuDAO menuDAO;
    private final OpenStreetMapService openStreetMapService;

    public PlaceService(PlaceDAO placeDAO, MyUserDAO myUserDAO,
                        CloudinaryService cloudinaryService, MenuDAO menuDAO,
                        OpenStreetMapService openStreetMapService) {
        this.placeDAO = placeDAO;
        this.myUserDAO = myUserDAO;
        this.cloudinaryService = cloudinaryService;
        this.menuDAO = menuDAO;
        this.openStreetMapService = openStreetMapService;
    }

    /**
     * Creates a new place (restaurant) owned by a specific user,
     * including its associated menu, handling file uploads, and geocoding the address.
     *
     * @param ownerUsername The username of the owner creating the place.
     * @param dto The PlaceCreateDTO containing place details and nested menu details.
     * @param imageFile Optional image file for the place.
     * @param videoFile Optional video file for the place.
     * @return A PlaceDTO representation of the created place.
     * @throws IllegalArgumentException if the owner is not found, role mismatch, or menu/address details are invalid.
     * @throws IOException if there's an error during file upload to Cloudinary.
     */
    @Transactional
    public PlaceDTO createPlace(String ownerUsername, PlaceCreateDTO dto,
                                 MultipartFile imageFile, MultipartFile videoFile) throws IOException {

        Optional<MyUser> ownerOpt = myUserDAO.findByUsernameIgnoreCase(ownerUsername);
        if (ownerOpt.isEmpty() || !ownerOpt.get().getRole().equals("OWNER")) {
            throw new IllegalArgumentException("Owner not found or does not have 'OWNER' role.");
        }
        MyUser owner = ownerOpt.get();

        Place place = new Place();
        place.setName(dto.getName());
        place.setOpeningHours(dto.getOpeningHours());
        place.setDescription(dto.getDescription());
        place.setOwner(owner);
        place.setAddress(dto.getAddress());

        // Geocode the address using OpenStreetMapService
        if (dto.getAddress() == null || dto.getAddress().isBlank()) {
            throw new IllegalArgumentException("Address is required for place creation.");
        }
        Map<String, Double> coordinates = openStreetMapService.getCoordinates(dto.getAddress());
        if (coordinates != null) {
            place.setLatitude(coordinates.get("latitude"));
            place.setLongitude(coordinates.get("longitude"));
        } else {
            throw new IllegalArgumentException("Could not geocode address: " + dto.getAddress());
        }

        // --- File Uploads ---
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(imageFile);
            place.setImageUrl(imageUrl);
        }
        if (videoFile != null && !videoFile.isEmpty()) {
            String videoUrl = cloudinaryService.uploadFile(videoFile);
            place.setVideoUrl(videoUrl);
        }

        Place savedPlace = placeDAO.save(place);

        // --- Create and associate Menu ---
        if (dto.getMenu() != null) {
            Menu menu = new Menu();
            menu.setItem(dto.getMenu().getItem());
            menu.setIngredients(dto.getMenu().getIngredients());
            menu.setPrice(dto.getMenu().getPrice());
            menu.setPlace(savedPlace);

            menuDAO.save(menu);
            savedPlace.setMenu(menu);
        } else {
            throw new IllegalArgumentException("Menu details are required for place creation.");
        }

        return toDTO(savedPlace);
    }

    /**
     * Updates an existing place (restaurant) owned by a specific user,
     * including its associated menu, handling file uploads, and re-geocoding if address changes.
     *
     * @param placeId The ID of the place to update.
     * @param ownerUsername The username of the owner attempting the update.
     * @param dto The PlaceCreateDTO containing updated place details and nested menu details.
     * @param imageFile Optional new image file for the place.
     * @param videoFile Optional new video file for the place.
     * @return A PlaceDTO representation of the updated place.
     * @throws IllegalArgumentException if the place is not found, user is not the owner, or geocoding fails.
     * @throws IOException if there's an error during file upload to Cloudinary.
     */
    @Transactional
    public PlaceDTO updatePlace(Long placeId, String ownerUsername, PlaceCreateDTO dto,
                                 MultipartFile imageFile, MultipartFile videoFile) throws IOException {
        Optional<Place> existingPlaceOpt = placeDAO.findById(placeId);
        if (existingPlaceOpt.isEmpty()) {
            throw new IllegalArgumentException("Place not found with ID: " + placeId);
        }
        Place existingPlace = existingPlaceOpt.get();

        if (!existingPlace.getOwner().getUsername().equalsIgnoreCase(ownerUsername)) {
            throw new IllegalArgumentException("User is not authorized to update this place.");
        }

        // Update basic place details if provided in DTO
        if (dto.getName() != null && !dto.getName().isEmpty()) {
            existingPlace.setName(dto.getName());
        }
        if (dto.getOpeningHours() != null && !dto.getOpeningHours().isEmpty()) {
            existingPlace.setOpeningHours(dto.getOpeningHours());
        }
        if (dto.getDescription() != null && !dto.getDescription().isEmpty()) {
            existingPlace.setDescription(dto.getDescription());
        }

        // Update address and re-geocode if address has changed
        if (dto.getAddress() != null && !dto.getAddress().isBlank() &&
            !dto.getAddress().equalsIgnoreCase(existingPlace.getAddress())) {

            existingPlace.setAddress(dto.getAddress());
            Map<String, Double> coordinates = openStreetMapService.getCoordinates(dto.getAddress());
            if (coordinates != null) {
                existingPlace.setLatitude(coordinates.get("latitude"));
                existingPlace.setLongitude(coordinates.get("longitude"));
            } else {
                throw new IllegalArgumentException("Could not re-geocode updated address: " + dto.getAddress());
            }
        }

        // --- Handle file uploads (for update) ---
        // If a new image file is provided, upload it and update the URL
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(imageFile);
            existingPlace.setImageUrl(imageUrl);
        }
        // NOTE: If you want to allow clearing an image/video without uploading a new one,
        // you would need a separate boolean flag in PlaceCreateDTO (e.g., 'clearImage')
        // or a different endpoint for clearing, as PlaceCreateDTO does not carry imageUrl/videoUrl.
        // For now, if no new file is provided, the existing URL remains unchanged.

        if (videoFile != null && !videoFile.isEmpty()) {
            String videoUrl = cloudinaryService.uploadFile(videoFile);
            existingPlace.setVideoUrl(videoUrl);
        }
        // See note above for clearing video.


        // --- Handle Menu update ---
        if (dto.getMenu() != null) {
            Menu menu = existingPlace.getMenu();
            if (menu == null) {
                menu = new Menu();
                menu.setPlace(existingPlace);
                existingPlace.setMenu(menu);
            }
            menu.setItem(dto.getMenu().getItem());
            menu.setIngredients(dto.getMenu().getIngredients());
            menu.setPrice(dto.getMenu().getPrice());
            menuDAO.save(menu);
        } else if (existingPlace.getMenu() != null) {
            menuDAO.delete(existingPlace.getMenu());
            existingPlace.setMenu(null);
        }

        return toDTO(placeDAO.save(existingPlace));
    }

    @Transactional(readOnly = true)
    public List<PlaceDTO> getAllPlaces() {
        List<Place> places = placeDAO.findAll();
        return places.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlaceDTO getPlaceById(Long id) {
        return placeDAO.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Place not found with ID: " + id));
    }

    @Transactional
    public void deletePlace(Long id) {
        if (!placeDAO.existsById(id)) {
            throw new IllegalArgumentException("Place not found with ID: " + id);
        }
        placeDAO.deleteById(id);
    }

    /**
     * Helper to convert a Place entity to a PlaceDTO.
     * Correctly maps all fields, including the single menu, new media URLs/owner, and coordinates.
     */
    private PlaceDTO toDTO(Place place) {
        PlaceDTO dto = new PlaceDTO();
        dto.setId(place.getId());
        dto.setName(place.getName());
        dto.setOpeningHours(place.getOpeningHours());
        dto.setDescription(place.getDescription());
        dto.setAddress(place.getAddress());
        dto.setImageUrl(place.getImageUrl());
        dto.setVideoUrl(place.getVideoUrl());
        dto.setLatitude(place.getLatitude());
        dto.setLongitude(place.getLongitude());

        if (place.getOwner() != null) {
            dto.setOwnerUsername(place.getOwner().getUsername());
        }
        if (place.getMenu() != null) {
            Menu menu = place.getMenu();
            MenuDTO menuDTO = new MenuDTO(
                menu.getId(),
                place.getId(),
                menu.getItem(),
                menu.getIngredients(),
                menu.getPrice()
            );
            dto.setMenu(menuDTO);
        }
        return dto;
    }
}