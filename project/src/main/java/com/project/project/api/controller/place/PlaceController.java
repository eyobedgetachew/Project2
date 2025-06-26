package com.project.project.api.controller.place;

import java.io.IOException; // NEW: Import for IOException
import java.util.List;

import org.springframework.http.HttpStatus; // NEW: Import for HttpStatus
import org.springframework.http.MediaType; // NEW: Import for MediaType
import org.springframework.http.ResponseEntity; // NEW: Import for ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize; // NEW: Import for PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal; // NEW: Import for AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails; // NEW: Import for UserDetails
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; // NEW: Import for PostMapping
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart; // NEW: Import for RequestPart
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile; // NEW: Import for MultipartFile

import com.project.project.api.DTO.PlaceCreateDTO; // NEW: Import PlaceCreateDTO
import com.project.project.api.DTO.PlaceDTO;
import com.project.project.service.PlaceService;

import jakarta.validation.Valid; // NEW: Import for @Valid

@RestController
@RequestMapping("/api/places")
public class PlaceController {
    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping // Existing method, updated to call getAllPlaces
    public List<PlaceDTO> getPlaces() {
        return placeService.getAllPlaces();
    }

    /**
     * NEW METHOD: Endpoint for an owner to create a new place with optional image/video and required menu.
     * Expects a multipart/form-data request.
     * The PlaceCreateDTO data should be sent as a part named "placeData".
     * Image and video files should be sent as parts named "imageFile" and "videoFile".
     *
     * @param userDetails The authenticated owner's details.
     * @param placeCreateDTO The DTO containing place details and nested menu details.
     * @param imageFile Optional image file.
     * @param videoFile Optional video file.
     * @return ResponseEntity with the created PlaceDTO.
     */
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('OWNER')") // Only owners can create places
    public ResponseEntity<PlaceDTO> createPlace(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("placeData") @Valid PlaceCreateDTO placeCreateDTO, // DTO as a RequestPart
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile, // Optional image file
            @RequestPart(value = "videoFile", required = false) MultipartFile videoFile // Optional video file
    ) {
        // Basic validation: ensure essential fields are present
        if (placeCreateDTO.getName() == null || placeCreateDTO.getName().isBlank()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        // Ensure menu and its item are present, as menu is required for Place
        if (placeCreateDTO.getMenu() == null || placeCreateDTO.getMenu().getItem() == null || placeCreateDTO.getMenu().getItem().isBlank()) {
             return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        try {
            PlaceDTO createdPlace = placeService.createPlace(
                    userDetails.getUsername(),
                    placeCreateDTO,
                    imageFile,
                    videoFile
            );
            return new ResponseEntity<>(createdPlace, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            System.err.println("Bad Request for place creation: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            System.err.println("File upload error for place creation: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            // Catch any other unexpected exceptions for robust error handling
            System.err.println("An unexpected error occurred during place creation: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}