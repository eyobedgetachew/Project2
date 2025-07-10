package com.project.project.api.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class MenuItemDTO {
    // No ID or placeId needed for creation DTO, as they are generated/set on backend
    // private Long id;
    // private Long placeId;

    @NotBlank(message = "Menu item name is required")
    private String item;

    @NotBlank(message = "Ingredients are required")
    private String ingredients;

    @NotBlank(message = "Price is required")
    @Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "Price must be a valid number with up to two decimal places")
    private String price; // Matches your Menu entity's String price

    public MenuItemDTO() {}

    // Constructor for convenience (optional, but good to have)
    public MenuItemDTO(String item, String ingredients, String price) {
        this.item = item;
        this.ingredients = ingredients;
        this.price = price;
    }

    // Getters & setters
    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
}
