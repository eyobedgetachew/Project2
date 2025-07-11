package com.project.project.api.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List; // NEW: Import List

public class PlaceCreateDTO {

    @NotBlank(message = "Place name is required")
    private String name;

    @NotBlank(message = "Cuisine type is required") // NEW: Add cuisine field and validation
    private String cuisine;

    @NotBlank(message = "Opening hours are required")
    private String openingHours;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotBlank(message = "Place address is required")
    private String address;

    @NotBlank(message = "Contact phone number is required") // NEW: Add contactInfo field and validation
    private String contactInfo;

    private String email; // NEW: Add email field (optional, no @NotBlank)

    @NotNull(message = "Latitude is required") // NEW: Add latitude field and validation
    private Double latitude;

    @NotNull(message = "Longitude is required") // NEW: Add longitude field and validation
    private Double longitude;

    // CHANGED: From single MenuCreateDTO to List of MenuItemDTOs
    @NotNull(message = "Menu items are required for a new place")
    @Size(min = 1, message = "At least one menu item is required") // Ensure at least one item
    @Valid // Validate each item in the list
    private List<MenuItemDTO> menuItems; // Renamed to plural

    // NEW: Add videoUrl field (no validation as it's handled by MultipartFile)
    private String videoUrl;


    public PlaceCreateDTO() {
    }

    // Constructor (consider removing if using setters, or update to include all fields)
    // For simplicity, often DTOs rely on default constructor and setters for deserialization.
    // If you use this constructor, ensure it matches all fields.
    public PlaceCreateDTO(String name, String cuisine, String openingHours, String description, String address,
                          String contactInfo, String email, Double latitude, Double Double,
                          List<MenuItemDTO> menuItems, String videoUrl) {
        this.name = name;
        this.cuisine = cuisine;
        this.openingHours = openingHours;
        this.description = description;
        this.address = address;
        this.contactInfo = contactInfo;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.menuItems = menuItems;
        this.videoUrl = videoUrl;
    }

    // --- Getters and Setters for ALL fields ---
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // NEW: Getter and Setter for cuisine
    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // NEW: Getter and Setter for contactInfo
    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    // NEW: Getter and Setter for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // NEW: Getters and Setters for latitude
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    // NEW: Getters and Setters for longitude
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    // CHANGED: Getter and Setter for menuItems (List)
    public List<MenuItemDTO> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItemDTO> menuItems) {
        this.menuItems = menuItems;
    }

    // NEW: Getter and Setter for videoUrl
    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
