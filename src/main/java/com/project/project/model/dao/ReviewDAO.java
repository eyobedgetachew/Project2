package com.project.project.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository; // Import Place if you want to find by Place object
import org.springframework.stereotype.Repository; // Import MyUser if you want to find by MyUser object

import com.project.project.model.MyUser;
import com.project.project.model.Place;
import com.project.project.model.Review;

@Repository // Marks this interface as a Spring Data JPA repository
public interface ReviewDAO extends JpaRepository<Review, Long> {

    // Custom query to find all reviews for a specific place (restaurant)
    // Spring Data JPA will automatically implement this method based on its name.
    List<Review> findByPlace(Place place);

    // Custom query to find all reviews by a specific user
    List<Review> findByUser(MyUser user);

    // You could also add methods like:
    // List<Review> findByPlaceId(Long placeId); // If you prefer to query by ID directly
    // List<Review> findByUserId(Long userId); // If you prefer to query by ID directly
    // List<Review> findByPlaceIdOrderByCreatedAtDesc(Long placeId); // To get latest reviews first

    // You can also add queries based on rating, if needed
    // List<Review> findByPlaceAndRatingGreaterThanEqual(Place place, Integer rating);
}