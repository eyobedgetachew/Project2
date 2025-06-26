package com.project.project.api.controller; // Ensure this matches your project structure

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // Assuming MyUser is your principal type
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // For @Valid annotation
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody; // To get the logged-in user
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.project.api.DTO.ReviewCreateDTO;
import com.project.project.api.DTO.ReviewDTO;
import com.project.project.model.MyUser;
import com.project.project.service.ReviewService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api") // Base path for your API
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Endpoint for a USER to create a new review for a place.
     * Accessible at POST /api/reviews
     * Requires 'USER' role.
     */
    @PostMapping("/reviews")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReviewDTO> createReview(
            @Valid @RequestBody ReviewCreateDTO reviewCreateDTO,
            @AuthenticationPrincipal MyUser principal // Injects the authenticated MyUser object
    ) {
        // The placeId is part of the reviewCreateDTO
        ReviewDTO createdReview = reviewService.createReview(principal.getUsername(), reviewCreateDTO);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED); // Return 201 Created
    }

    /**
     * Endpoint to get all reviews for a specific place.
     * Accessible at GET /api/places/{placeId}/reviews
     * Requires 'USER' role (can be adjusted to permitAll if reviews are public).
     */
    @GetMapping("/places/{placeId}/reviews")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ReviewDTO>> getReviewsForPlace(@PathVariable Long placeId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByPlaceId(placeId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Endpoint to get all reviews made by the currently authenticated user.
     * Accessible at GET /api/reviews/my
     * Requires 'USER' role.
     */
    @GetMapping("/reviews/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ReviewDTO>> getMyReviews(
            @AuthenticationPrincipal MyUser principal
    ) {
        List<ReviewDTO> reviews = reviewService.getReviewsByUsername(principal.getUsername());
        return ResponseEntity.ok(reviews);
    }

    // You might also want an endpoint to get a single review by ID later,
    // or update/delete reviews depending on your requirements.
}