package com.project.project.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.project.api.DTO.MenuItemDTO;
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
    // private final MenuDAO menuDAO; // Not strictly needed for basic CRUD due to cascade, but keep if custom methods exist

    public PlaceService(PlaceDAO placeDAO, MyUserDAO myUserDAO,
                        CloudinaryService cloudinaryService, MenuDAO menuDAO) {
        this.placeDAO = placeDAO;
        this.myUserDAO = myUserDAO;
        this.cloudinaryService = cloudinaryService;
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
                .orElse(null);
    }

    /**
     * Retrieves a single Place owned by a specific user.
     * @param ownerUsername The username of the owner.
     * @return PlaceDTO if found, null otherwise.
     * @throws IllegalArgumentException if the owner is not found.
     */
    @Transactional(readOnly = true)
    public PlaceDTO getPlaceByOwnerUsername(String ownerUsername) {
        MyUser owner = myUserDAO.findByUsernameIgnoreCase(ownerUsername)
                .orElseThrow(() -> new IllegalArgumentException("Owner user not found."));
        
        // Assuming an owner can only have one restaurant.
        // If an owner can have multiple, this logic needs to be adjusted (e.g., return List<PlaceDTO>).
        return placeDAO.findByOwner(owner)
                .map(this::toDTO)
                .orElse(null);
    }

    @Transactional
    public PlaceDTO createPlace(
            String ownerUsername,
            PlaceCreateDTO placeCreateDTO,
            MultipartFile imageFile,
            MultipartFile videoFile
    ) throws IOException {
        MyUser owner = myUserDAO.findByUsernameIgnoreCase(ownerUsername)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found: " + ownerUsername));

        Place place = new Place();
        place.setName(placeCreateDTO.getName());
        place.setCuisine(placeCreateDTO.getCuisine());
        place.setOpeningHours(placeCreateDTO.getOpeningHours());
        place.setDescription(placeCreateDTO.getDescription());
        place.setAddress(placeCreateDTO.getAddress());
        place.setContactInfo(placeCreateDTO.getContactInfo());
        place.setEmail(placeCreateDTO.getEmail());
        place.setLatitude(placeCreateDTO.getLatitude());
        place.setLongitude(placeCreateDTO.getLongitude());
        place.setOwner(owner);

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(imageFile);
            place.setImageUrl(imageUrl);
        }

        if (videoFile != null && !videoFile.isEmpty()) {
            String videoUrl = cloudinaryService.uploadFile(videoFile);
            place.setVideoUrl(videoUrl);
        }

        if (placeCreateDTO.getMenuItems() != null && !placeCreateDTO.getMenuItems().isEmpty()) {
            List<Menu> menuItems = placeCreateDTO.getMenuItems().stream()
                .map(menuItemDTO -> {
                    Menu menu = new Menu();
                    menu.setItem(menuItemDTO.getItem());
                    menu.setIngredients(menuItemDTO.getIngredients());
                    menu.setPrice(menuItemDTO.getPrice());
                    menu.setPlace(place);
                    return menu;
                })
                .collect(Collectors.toList());
            place.setMenuItems(menuItems);
        } else {
            place.setMenuItems(Collections.emptyList());
        }

        Place savedPlace = placeDAO.save(place);
        return toDTO(savedPlace);
    }

    /**
     * Updates an existing place (restaurant) owned by a specific user.
     *
     * @param placeId The ID of the place to update.
     * @param ownerUsername The username of the owner attempting the update.
     * @param dto The PlaceCreateDTO containing updated place details.
     * @param imageFile Optional new image file.
     * @param videoFile Optional new video file.
     * @return A PlaceDTO representation of the updated place.
     * @throws IllegalArgumentException if the place is not found, user is not the owner, or details are invalid.
     * @throws IOException if there's an error during file upload.
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

        if (dto.getName() != null && !dto.getName().isEmpty()) {
            existingPlace.setName(dto.getName());
        }
        if (dto.getCuisine() != null && !dto.getCuisine().isEmpty()) {
            existingPlace.setCuisine(dto.getCuisine());
        }
        if (dto.getOpeningHours() != null && !dto.getOpeningHours().isEmpty()) {
            existingPlace.setOpeningHours(dto.getOpeningHours());
        }
        if (dto.getDescription() != null && !dto.getDescription().isEmpty()) {
            existingPlace.setDescription(dto.getDescription());
        }
        if (dto.getAddress() != null && !dto.getAddress().isEmpty()) {
            existingPlace.setAddress(dto.getAddress());
        }
        if (dto.getContactInfo() != null && !dto.getContactInfo().isEmpty()) {
            existingPlace.setContactInfo(dto.getContactInfo());
        }
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            existingPlace.setEmail(dto.getEmail());
        }
        if (dto.getLatitude() != null) {
            existingPlace.setLatitude(dto.getLatitude());
        }
        if (dto.getLongitude() != null) {
            existingPlace.setLongitude(dto.getLongitude());
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(imageFile);
            existingPlace.setImageUrl(imageUrl);
        }
        if (videoFile != null && !videoFile.isEmpty()) {
            String videoUrl = cloudinaryService.uploadFile(videoFile);
            existingPlace.setVideoUrl(videoUrl);
        }

        existingPlace.getMenuItems().clear(); 
        if (dto.getMenuItems() != null && !dto.getMenuItems().isEmpty()) {
            for (MenuItemDTO menuItemDTO : dto.getMenuItems()) {
                Menu menu = new Menu();
                menu.setItem(menuItemDTO.getItem());
                menu.setIngredients(menuItemDTO.getIngredients());
                menu.setPrice(menuItemDTO.getPrice());
                menu.setPlace(existingPlace);
                existingPlace.addMenuItem(menu);
            }
        } else {
            // If the DTO sends an empty list of menu items, it means all should be removed.
        }

        return toDTO(placeDAO.save(existingPlace));
    }

    @Transactional
    public void deletePlace(Long id) {
        if (!placeDAO.existsById(id)) {
            throw new IllegalArgumentException("Place not found with ID: " + id);
        }
        placeDAO.deleteById(id);
    }

    private PlaceDTO toDTO(Place place) {
        PlaceDTO dto = new PlaceDTO();
        dto.setId(place.getId());
        dto.setName(place.getName());
        dto.setCuisine(place.getCuisine());
        dto.setOpeningHours(place.getOpeningHours());
        dto.setDescription(place.getDescription());
        dto.setAddress(place.getAddress());
        dto.setContactInfo(place.getContactInfo());
        dto.setEmail(place.getEmail());
        dto.setImageUrl(place.getImageUrl());
        dto.setVideoUrl(place.getVideoUrl());
        dto.setLatitude(place.getLatitude());
        dto.setLongitude(place.getLongitude());

        if (place.getOwner() != null) {
            dto.setOwnerUsername(place.getOwner().getUsername());
        } else {
            dto.setOwnerUsername(null);
        }
        
        if (place.getMenuItems() != null && !place.getMenuItems().isEmpty()) {
            List<MenuItemDTO> menuItemDTOs = place.getMenuItems().stream()
                .map(menuItem -> {
                    MenuItemDTO itemDTO = new MenuItemDTO();
                    itemDTO.setItem(menuItem.getItem());
                    itemDTO.setIngredients(menuItem.getIngredients());
                    itemDTO.setPrice(menuItem.getPrice());
                    return itemDTO;
                })
                .collect(Collectors.toList());
            dto.setMenuItems(menuItemDTOs);
        } else {
            dto.setMenuItems(Collections.emptyList());
        }
        
        return dto;
    }
}
