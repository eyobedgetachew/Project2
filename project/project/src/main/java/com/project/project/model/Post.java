package com.project.project.model;

import java.time.LocalDateTime; // NEW: Import for LocalDateTime
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable; // If you use it for bidirectional relationships
import jakarta.persistence.Column; // Keep: For tags
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity; // Keep: For tags
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "post") // Or whatever your table name is
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    // NEW: Fields for image and video URLs
    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "video_url")
    private String videoUrl;

    @ManyToOne(optional = false, fetch = FetchType.LAZY) // Added FetchType.LAZY
    @JoinColumn(name = "user_id")
    private MyUser user;

    // This is the place/restaurant that the post belongs to (nullable for user posts)
    @ManyToOne(fetch = FetchType.LAZY) // Added FetchType.LAZY
    @JoinColumn(name = "place_id", nullable = true)
    private Place place;

    @ElementCollection // Indicates a collection of basic types or embeddables
    @CollectionTable( 
 name = "post_tags", 
 joinColumns = @JoinColumn(name = "post_id") )
    @Column(name = "tag_name") // Names the column in the 'post_tags' table that stores the tag string
    private List<String> tags = new ArrayList<>();

    // NEW: Timestamp for when the post was created
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // --- Constructors ---
    public Post() {
        this.createdAt = LocalDateTime.now(); // Initialize on creation
    }

    public Post(String title, String content, MyUser user, Place place, List<String> tags) { // Updated constructor
        this.title = title;
        this.content = content;
        this.user = user;
        this.place = place;
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>(); // Initialize tags safely
        this.createdAt = LocalDateTime.now(); // Initialize on creation
    }


    // --- Getters and Setters ---
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // NEW: Getters and Setters for imageUrl
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // NEW: Getters and Setters for videoUrl
    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    // Existing: Getters and Setters for tags
    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    // NEW: Getter and Setter for createdAt
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}