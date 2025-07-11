package com.project.project.api.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PlaceCreateDTO {

    @NotBlank(message = "Place name is required")
    private String name;

    private String openingHours;
    private String description;

    @NotBlank(message = "Place address is required") // NEW: Add validation for address
    private String address; // NEW: Add this field for the place's address

    @NotNull(message = "Menu details are required for a new place")
    @Valid
    private MenuCreateDTO menu;

    public PlaceCreateDTO() {
    }

    // UPDATED Constructor: Now includes address, openingHours, description, and MenuCreateDTO
    public PlaceCreateDTO(String name, String openingHours, String description, String address, MenuCreateDTO menu) { // NEW: Add 'address' parameter
        this.name = name;
        this.openingHours = openingHours;
        this.description = description;
        this.address = address; // NEW: Initialize address
        this.menu = menu;
    }

    // --- Getters and Setters ---
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

    // NEW: Getters and Setters for address
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public MenuCreateDTO getMenu() {
        return menu;
    }

    public void setMenu(MenuCreateDTO menu) {
        this.menu = menu;
    }
}