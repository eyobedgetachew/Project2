package com.project.project.api.controller.place;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.project.api.DTO.PlaceCreateDTO;
import com.project.project.api.DTO.PlaceDTO;
import com.project.project.model.MyUser;
import com.project.project.service.PlaceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/places")
public class PlaceController {
    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping
    public ResponseEntity<List<PlaceDTO>> getPlaces() {
        List<PlaceDTO> places = placeService.getAllPlaces();
        return ResponseEntity.ok(places);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaceDTO> getPlaceById(@PathVariable Long id) {
        PlaceDTO placeDTO = placeService.getPlaceById(id);
        if (placeDTO != null) {
            return ResponseEntity.ok(placeDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint for an owner to retrieve their own restaurant's details.
     * @param currentUser The authenticated owner.
     * @return ResponseEntity with the PlaceDTO of the owner's restaurant, or 404 if not found.
     */
    @GetMapping("/my-restaurant")
    @PreAuthorize("hasRole('OWNER')") // Only owners can access this
    public ResponseEntity<PlaceDTO> getMyRestaurant(@AuthenticationPrincipal MyUser currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        PlaceDTO placeDTO = placeService.getPlaceByOwnerUsername(currentUser.getUsername());
        if (placeDTO != null) {
            return ResponseEntity.ok(placeDTO);
        } else {
            return ResponseEntity.notFound().build(); // No restaurant found for this owner
        }
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Map<String, String>> createPlace(
            @AuthenticationPrincipal MyUser currentUser,
            @RequestPart("dto") @Valid PlaceCreateDTO placeCreateDTO,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            @RequestPart(value = "video", required = false) MultipartFile videoFile
    ) {
        try {
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "User not authenticated."));
            }
            if (!"OWNER".equalsIgnoreCase(currentUser.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", "Only owners can create places."));
            }

            placeService.createPlace(
                    currentUser.getUsername(),
                    placeCreateDTO,
                    imageFile,
                    videoFile
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonMap("message", "Place created successfully!"));
        } catch (IllegalArgumentException e) {
            System.err.println("Bad Request for place creation: " + e.getMessage());
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        } catch (IOException e) {
            System.err.println("File upload error for place creation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Failed to upload file: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during place creation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "An unexpected error occurred."));
        }
    }

    /**
     * Endpoint for an owner to update their existing place.
     * Uses PATCH for partial updates and supports multipart/form-data for files.
     *
     * @param id The ID of the place to update.
     * @param currentUser The authenticated owner.
     * @param placeUpdateDTO The DTO containing updated place details.
     * @param imageFile Optional new image file.
     * @param videoFile Optional new video file.
     * @return ResponseEntity with a success message or error.
     */
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Map<String, String>> updatePlace(
            @PathVariable Long id,
            @AuthenticationPrincipal MyUser currentUser,
            @RequestPart("dto") @Valid PlaceCreateDTO placeUpdateDTO,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            @RequestPart(value = "video", required = false) MultipartFile videoFile
    ) {
        try {
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "User not authenticated."));
            }
            if (!"OWNER".equalsIgnoreCase(currentUser.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", "Only owners can update places."));
            }

            placeService.updatePlace(
                    id,
                    currentUser.getUsername(),
                    placeUpdateDTO,
                    imageFile,
                    videoFile
            );
            return ResponseEntity.ok(Collections.singletonMap("message", "Place updated successfully!"));
        } catch (IllegalArgumentException e) {
            System.err.println("Bad Request for place update: " + e.getMessage());
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        } catch (IOException e) {
            System.err.println("File upload error for place update: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Failed to upload file: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during place update: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "An unexpected error occurred."));
        }
    }
}
