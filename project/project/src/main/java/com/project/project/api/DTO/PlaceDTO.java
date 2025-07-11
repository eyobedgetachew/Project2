package com.project.project.api.DTO;

public class PlaceDTO {

    private Long id;
    private String name;
    private String openingHours;
    private String description;
    private String address; // NEW: Add this field for the place's address
    private String ownerUsername;

    private String imageUrl; // NEW: Field for image URL
    private String videoUrl; // NEW: Field for video URL

    private Double latitude; // NEW: Add this field
    private Double longitude; // NEW: Add this field
    private MenuDTO menu;

    public PlaceDTO() {
    }

    // UPDATED Constructor: Includes all new fields
    public PlaceDTO(Long id, String name, String openingHours, String description, String address,
                    String ownerUsername, String imageUrl, String videoUrl,
                    Double latitude, Double longitude, // Placed together for logical grouping
                    MenuDTO menu) {
        this.id = id;
        this.name = name;
        this.openingHours = openingHours;
        this.description = description;
        this.address = address; // Initialize address
        this.ownerUsername = ownerUsername;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.latitude = latitude; // Initialize latitude
        this.longitude = longitude; // Initialize longitude
        this.menu = menu;
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

    public MenuDTO getMenu() {
        return menu;
    }

    public void setMenu(MenuDTO menu) {
        this.menu = menu;
    }
}