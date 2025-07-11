package com.project.project.model;

import java.util.ArrayList;
import java.util.List; // Keep if you use it elsewhere

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
import jakarta.persistence.JoinColumn; // NEW: For @JoinColumn for owner
import jakarta.persistence.ManyToOne; // NEW: For @ManyToOne owner relationship
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(name = "Place") // Table name is "Place"
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "names", nullable = false, unique = true) // Column name is "names"
    private String name;

    @Column(name = "opening_hours") // Existing field
    private String openingHours;

    @Column(name = "description", nullable = false) // Existing field
    private String description;

    // --- NEW: Add owner relationship ---
    @ManyToOne(fetch = FetchType.LAZY) // Or EAGER if you always need owner data
    @JoinColumn(name = "owner_id", nullable = false) // Assuming a foreign key column named owner_id
    private MyUser owner;

    // --- NEW: Add imageUrl and videoUrl for Cloudinary ---
    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "address", nullable = false) // Assuming you have an address field
    private String address; // Make sure you have this if not already present

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    // Existing: OneToOne relationship with Menu
    @OneToOne(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("place-menu")
    private Menu menu;

    // Existing: OneToMany relationship with Reservations
    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("place-reservations")
    private List<Reservation> reservations = new ArrayList<>();

    // --- Constructors (add if not present, adjust if already present) ---
    public Place() {
    }

    // You might want a constructor for convenience, e.g.:
    public Place(String name, String openingHours, String description, MyUser owner) {
        this.name = name;
        this.openingHours = openingHours;
        this.description = description;
        this.owner = owner;
    }


    // --- Getters and Setters (ensure all fields have them) ---

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

    // NEW: Getters and Setters for owner
    public MyUser getOwner() {
        return owner;
    }

    public void setOwner(MyUser owner) {
        this.owner = owner;
    }

    // NEW: Getters and Setters for imageUrl and videoUrl
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

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    // Existing: Getters and setters for imageUrl and videoUrl

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
    // Removed the unimplemented method (getPlacesWithMenus) from here as it's not part of the entity's direct responsibility
    // public Collection<PlaceDTO> getPlacesWithMenus() {
    // throw new UnsupportedOperationException("Unimplemented method 'getPlacesWithMenus'");
    // }

    // Consider adding add/remove methods for reservations for convenience:
    public void addReservation(Reservation reservation) {
        this.reservations.add(reservation);
        reservation.setPlace(this);
    }

    public void removeReservation(Reservation reservation) {
        this.reservations.remove(reservation);
        reservation.setPlace(null);
    }
}