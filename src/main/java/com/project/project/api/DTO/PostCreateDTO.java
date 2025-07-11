package com.project.project.api.DTO;

import java.util.List; // NEW: Import for @NotBlank

import jakarta.validation.constraints.NotBlank;    // NEW: Import for @Size
import jakarta.validation.constraints.Size;

public class PostCreateDTO {

    @NotBlank(message = "Title is required") // Ensures title is not null or empty/whitespace
    @Size(max = 100, message = "Title cannot exceed 100 characters") // Limits title length
    private String title;

    @NotBlank(message = "Content is required") // Ensures content is not null or empty/whitespace
    @Size(max = 1000, message = "Content cannot exceed 1000 characters") // Limits content length
    private String content;

    private Long placeId; // Optional: If posts can be linked to a place

    private List<String> tags; // For post tags

    public PostCreateDTO() {
    }

    // Constructor (if you use it)
    public PostCreateDTO(String title, String content, Long placeId, List<String> tags) {
        this.title = title;
        this.content = content;
        this.placeId = placeId;
        this.tags = tags;
    }

    // --- Getters and Setters ---
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}