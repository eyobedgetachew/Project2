package com.project.project.api.DTO;

import java.util.ArrayList; // NEW: Import List
import java.util.List; // NEW: Import ArrayList for initialization

public class PlaceDTO {

    private Long id;
    private String name;
    private String cuisine; // NEW: Add cuisine field
    private String openingHours;
    private String description;
    private String address;
    private String contactInfo; // NEW: Add contactInfo field
    private String email; // NEW: Add email field
    private String ownerUsername;

    private String imageUrl;
    private String videoUrl;

    private Double latitude;
    private Double longitude;

    // CHANGED: From single MenuDTO to List of MenuItemDTO
    private List<MenuItemDTO> menuItems = new ArrayList<>(); // Initialize to prevent null pointer

    public PlaceDTO() {
    }

    // UPDATED Constructor: Includes all new fields and List of menuItems
    public PlaceDTO(Long id, String name, String cuisine, String openingHours, String description, String address,
                    String contactInfo, String email, String ownerUsername, String imageUrl, String videoUrl,
                    Double latitude, Double longitude, List<MenuItemDTO> menuItems) {
        this.id = id;
        this.name = name;
        this.cuisine = cuisine;
        this.openingHours = openingHours;
        this.description = description;
        this.address = address;
        this.contactInfo = contactInfo;
        this.email = email;
        this.ownerUsername = ownerUsername;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        // Ensure menuItems list is not null
        this.menuItems = menuItems != null ? new ArrayList<>(menuItems) : new ArrayList<>();
    }

    // --- Getters and Setters (all unique) ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

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
}
