package com.project.project.api.DTO;

public class MenuDTO {
    private Long id;
    private Long placeId;
    private String item;
    private String ingredients;
    private String price;

    public MenuDTO() {}

    public MenuDTO(Long id, Long placeId, String item, String ingredients, String price) {
        this.id = id;
        this.placeId = placeId;
        this.item = item;
        this.ingredients = ingredients;
        this.price = price;
    }

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPlaceId() { return placeId; }
    public void setPlaceId(Long placeId) { this.placeId = placeId; }

    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
}
