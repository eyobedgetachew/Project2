package com.project.project.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "reviews") // Define table name for the entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer rating; // e.g., 1 to 5 stars

    @Column(columnDefinition = "TEXT") // Use TEXT for potentially longer comments
    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Foreign key to MyUser
    private MyUser user;

    @ManyToOne
    @JoinColumn(name = "place_id", nullable = false) // Foreign key to Place
    private Place place;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // --- Constructors ---
    public Review() {
        this.createdAt = LocalDateTime.now(); // Set creation timestamp by default
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}