package com.project.project.model;

import java.util.ArrayList;
import java.util.Collection; // NEW: Import for Collection
import java.util.Collections; // NEW: Import for Collections
import java.util.List;

import org.springframework.security.core.GrantedAuthority; // NEW
import org.springframework.security.core.authority.SimpleGrantedAuthority; // NEW
import org.springframework.security.core.userdetails.UserDetails; // NEW

import com.fasterxml.jackson.annotation.JsonIdentityInfo; // NEW: Import
import com.fasterxml.jackson.annotation.JsonIgnore; // NEW: Import
import com.fasterxml.jackson.annotation.ObjectIdGenerators; // NEW: Import

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(name = "my_user")
public class MyUser implements UserDetails { // UPDATED: Implement UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @JsonIgnore
    @Column(name = "password", nullable = false, length = 1000)
    private String password;

    @Column(name = "email", nullable = false, unique = true, length = 320)
    private String email;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "role", nullable = false)
    private String role = "USER";

    @ElementCollection
    @Column(name = "interest")
    private List<String> interest = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Place> places = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Post> posts = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id desc")
    private List<VerificationToken> verificationTokens = new ArrayList<>();

    @Column(name = "profile_picture_url", length = 1000)


    @ManyToMany
@JoinTable(
    name = "user_liked_posts", // Name of the new join table
    joinColumns = @JoinColumn(name = "user_id"), // Column for MyUser ID in the join table
    inverseJoinColumns = @JoinColumn(name = "post_id") // Column for Post ID in the join table
)
@JsonIgnore // Important: Prevent infinite recursion when MyUser is serialized to JSON
private List<Post> likedPosts = new ArrayList<>();

public List<Post> getLikedPosts() {
    return likedPosts;
}

public void setLikedPosts(List<Post> likedPosts) {
    this.likedPosts = likedPosts;
}

// Optional: Helper methods for managing the collection
public void addLikedPost(Post post) {
    if (this.likedPosts == null) {
        this.likedPosts = new ArrayList<>();
    }
    if (!this.likedPosts.contains(post)) {
        this.likedPosts.add(post);
    }
}

public void removeLikedPost(Post post) {
    if (this.likedPosts != null) {
        this.likedPosts.remove(post);
    }
}

private String profilePictureUrl;


public String getProfilePictureUrl() {
    return profilePictureUrl;
}

public void setProfilePictureUrl(String profilePictureUrl) {
    this.profilePictureUrl = profilePictureUrl;
}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // @Override // No @Override needed here, as UserDetails has no getId()
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override // NEW: Override from UserDetails
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getInterests() {
        return interest;
    }

    public void setInterests(List<String> interests) {
        this.interest = interests;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public List<VerificationToken> getVerificationTokens() {
        return verificationTokens;
    }

    public void setVerificationTokens(List<VerificationToken> verificationTokens) {
        this.verificationTokens = verificationTokens;
    }

    public void addPlace(Place place) {
        this.places.add(place);
        place.setOwner(this);
    }

    public void removePlace(Place place) {
        this.places.remove(place);
        place.setOwner(null);
    }

    public void addPost(Post post) {
        this.posts.add(post);
        post.setUser(this);
    }

    public void removePost(Post post) {
        this.posts.remove(post);
        post.setUser(null);
    }

    // --- UserDetails Interface Implementations ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring Security roles should be prefixed with "ROLE_"
        // e.g., if role is "USER", it becomes "ROLE_USER"
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }

    // This method is already present, but adding @Override for clarity
    @Override
    public boolean isAccountNonExpired() {
        // Return true if accounts do not expire, or implement your expiration logic
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Return true if accounts are never locked, or implement your locking logic
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Return true if credentials do not expire, or implement your credential expiration logic
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Return true if the user is enabled (e.g., email verified)
        return this.emailVerified; // Use emailVerified as the enabled status
    }
}