package com.project.project.api.DTO;

import jakarta.validation.constraints.NotBlank;

public class MenuCreateDTO {

    @NotBlank(message = "Menu item name is required")
    private String item;

    private String ingredients;

    @NotBlank(message = "Menu item price is required")
    // Consider using BigDecimal for currency for precision
    // private BigDecimal price;
    private String price; // Using String as per previous examples, adjust if needed

    public MenuCreateDTO() {
    }

    public MenuCreateDTO(String item, String ingredients, String price) {
        this.item = item;
        this.ingredients = ingredients;
        this.price = price;
    }

    // Getters and Setters
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}