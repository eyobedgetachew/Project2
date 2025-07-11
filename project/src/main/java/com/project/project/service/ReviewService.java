package com.project.project.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.project.api.DTO.ReviewCreateDTO;
import com.project.project.api.DTO.ReviewDTO; // Don't forget to import ReviewDAO
import com.project.project.model.MyUser;
import com.project.project.model.Place; // Good practice for write operations
import com.project.project.model.Review;
import com.project.project.model.dao.MyUserDAO;
import com.project.project.model.dao.PlaceDAO;
import com.project.project.model.dao.ReviewDAO;

@Service
public class ReviewService {

    private final ReviewDAO reviewDAO;
    private final MyUserDAO myUserDAO;
    private final PlaceDAO placeDAO;

    // Constructor Injection
    public ReviewService(ReviewDAO reviewDAO, MyUserDAO myUserDAO, PlaceDAO placeDAO) {
        this.reviewDAO = reviewDAO;
        this.myUserDAO = myUserDAO;
        this.placeDAO = placeDAO;
    }

    /**
     * Creates a new review for a place by a specific user.
     *
     * @param username The username of the user creating the review.
     * @param dto      The ReviewCreateDTO containing placeId, rating, and comment.
     * @return A ReviewDTO representation of the created review.
     * @throws IllegalArgumentException if the user or place is not found.
     */
    @Transactional // Ensures the entire operation is a single transaction
    public ReviewDTO createReview(String username, ReviewCreateDTO dto) {
        // 1. Find the User
        Optional<MyUser> userOpt = myUserDAO.findByUsernameIgnoreCase(username);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + username);
        }
        MyUser user = userOpt.get();

        // 2. Find the Place
        Optional<Place> placeOpt = placeDAO.findById(dto.getPlaceId());
        if (placeOpt.isEmpty()) {
            throw new IllegalArgumentException("Place not found with ID: " + dto.getPlaceId());
        }
        Place place = placeOpt.get();

        // 3. Create the Review entity
        Review review = new Review();
        review.setUser(user);
        review.setPlace(place);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setCreatedAt(LocalDateTime.now()); // Ensure timestamp is set

        // 4. Save the Review
        Review savedReview = reviewDAO.save(review);

        // 5. Convert to DTO and return
        return toDTO(savedReview);
    }

    /**
     * Retrieves all reviews for a specific place.
     *
     * @param placeId The ID of the place to retrieve reviews for.
     * @return A list of ReviewDTOs for the specified place.
     * @throws IllegalArgumentException if the place is not found.
     */
    @Transactional(readOnly = true) // Read-only transaction for fetching data
    public List<ReviewDTO> getReviewsByPlaceId(Long placeId) {
        Optional<Place> placeOpt = placeDAO.findById(placeId);
        if (placeOpt.isEmpty()) {
            throw new IllegalArgumentException("Place not found with ID: " + placeId);
        }
        Place place = placeOpt.get();

        return reviewDAO.findByPlace(place)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all reviews made by a specific user.
     *
     * @param username The username of the user whose reviews to retrieve.
     * @return A list of ReviewDTOs made by the specified user.
     * @throws IllegalArgumentException if the user is not found.
     */
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByUsername(String username) {
        Optional<MyUser> userOpt = myUserDAO.findByUsernameIgnoreCase(username);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + username);
        }
        MyUser user = userOpt.get();

        return reviewDAO.findByUser(user)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to convert a Review entity to a ReviewDTO.
     *
     * @param review The Review entity to convert.
     * @return The corresponding ReviewDTO.
     */
    private ReviewDTO toDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());

        // Set user and place details if available
        if (review.getUser() != null) {
            dto.setUsername(review.getUser().getUsername());
        }
        if (review.getPlace() != null) {
            dto.setPlaceId(review.getPlace().getId());
            dto.setPlaceName(review.getPlace().getName());
        }
        return dto;
    }
}