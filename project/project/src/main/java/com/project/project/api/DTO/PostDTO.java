package com.project.project.api.DTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostDTO {
    private Long id;
    private String title;
    private String username;
    private String content;
    private String placeName;    // optional, only for OWNER posts
    private Long placeId;        // NEW: Add placeId for reference
    private LocalDateTime createdAt;
    private String imageUrl;     // NEW: Field for image URL
    private String videoUrl;     // NEW: Field for video URL
    private List<String> tags = new ArrayList<>(); // Tags for the post

    public PostDTO() {}

    // UPDATED Constructor: Now includes all fields
    public PostDTO(Long id, String title, String username, String content,
                   String placeName, Long placeId, LocalDateTime createdAt,
                   String imageUrl, String videoUrl, List<String> tags) {
        this.id = id;
        this.title = title;
        this.username = username;
        this.content = content;
        this.placeName = placeName;
        this.placeId = placeId;
        this.createdAt = createdAt;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        // Ensure tags list is not null
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
    }

// Inside your PostDTO class, alongside other private fields
private String userProfilePictureUrl;

// ... (other getters and setters)

// Add these new getter and setter
public String getUserProfilePictureUrl() {
    return userProfilePictureUrl;
}

public void setUserProfilePictureUrl(String userProfilePictureUrl) {
    this.userProfilePictureUrl = userProfilePictureUrl;
}


    // --- Getters and Setters for ALL fields ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    // NEW: Getter and Setter for placeId
    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // NEW: Getter and Setter for imageUrl
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // NEW: Getter and Setter for videoUrl
    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}