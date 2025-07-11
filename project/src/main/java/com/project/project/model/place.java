package com.project.project.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany; // Changed from OneToOne
import jakarta.persistence.Table;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(name = "Place")
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true) // Changed from "names" to "name" for consistency
    private String name;

    @Column(name = "cuisine", nullable = false) // NEW: Add cuisine field
    private String cuisine;

    @Column(name = "opening_hours")
    private String openingHours;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT") // Added columnDefinition for larger text
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private MyUser owner;

    @Column(name = "image_url", columnDefinition = "TEXT") // Added columnDefinition for larger URLs
    private String imageUrl;

    @Column(name = "video_url", columnDefinition = "TEXT") // Added columnDefinition for larger URLs
    private String videoUrl;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "contact_info", nullable = false) // NEW: Add contactInfo field
    private String contactInfo;

    @Column(name = "email") // NEW: Add email field
    private String email;

    // CHANGED: OneToOne relationship with Menu to OneToMany with List<Menu>
    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("place-menu")
    private List<Menu> menuItems = new ArrayList<>(); // Renamed to plural and initialized

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("place-reservations")
    private List<Reservation> reservations = new ArrayList<>();

    // Constructors
    public Place() {
    }

    public Place(String name, String cuisine, String openingHours, String description, String address, String contactInfo, String email, Double latitude, Double longitude, MyUser owner) {
        this.name = name;
        this.cuisine = cuisine;
        this.openingHours = openingHours;
        this.description = description;
        this.address = address;
        this.contactInfo = contactInfo;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.owner = owner;
    }

    // Getters and Setters
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

    public MyUser getOwner() {
        return owner;
    }

    public void setOwner(MyUser owner) {
        this.owner = owner;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    // NEW: Getters and Setters for contactInfo and email
    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // CHANGED: Getter and Setter for menuItems (List)
    public List<Menu> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<Menu> menuItems) {
        this.menuItems = menuItems;
    }

    // Helper methods for menuItems (add if not already present)
    public void addMenuItem(Menu menuItem) {
        if (menuItem != null) {
            menuItems.add(menuItem);
            menuItem.setPlace(this); // Set the back-reference
        }
    }

    public void removeMenuItem(Menu menuItem) {
        if (menuItem != null) {
            menuItems.remove(menuItem);
            menuItem.setPlace(null); // Remove the back-reference
        }
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public void addReservation(Reservation reservation) {
        this.reservations.add(reservation);
        reservation.setPlace(this);
    }

    public void removeReservation(Reservation reservation) {
        this.reservations.remove(reservation);
        reservation.setPlace(null);
    }
}
