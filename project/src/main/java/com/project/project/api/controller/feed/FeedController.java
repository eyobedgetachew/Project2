package com.project.project.api.controller.feed;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails; // Keep this for existing methods
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.project.api.DTO.PlaceDTO;
import com.project.project.api.DTO.PostCreateDTO;
import com.project.project.api.DTO.PostDTO;
import com.project.project.model.MyUser; // NEW: Import MyUser to use with @AuthenticationPrincipal
import com.project.project.service.FeedService;
import com.project.project.service.PostService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/feed") // Base path for feed-related endpoints
public class FeedController {

    private final FeedService feedService;
    private final PostService postService;

    public FeedController(FeedService feedService, PostService postService) {
        this.feedService = feedService;
        this.postService = postService;
    }

    @GetMapping("/places")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<PlaceDTO>> getPlaces() {
        List<PlaceDTO> places = feedService.getAllPlaces();
        return ResponseEntity.ok(places);
    }

    @PostMapping(value = "/user/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('USER', 'OWNER')")
    public ResponseEntity<PostDTO> createUserPost(
            @AuthenticationPrincipal UserDetails userDetails, // Keep UserDetails for this method
            @RequestPart("postData") @Valid PostCreateDTO postCreateDTO,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestPart(value = "videoFile", required = false) MultipartFile videoFile
    ) {
        if ((postCreateDTO.getContent() == null || postCreateDTO.getContent().isBlank()) &&
            (postCreateDTO.getTitle() == null || postCreateDTO.getTitle().isBlank()) &&
            (imageFile == null || imageFile.isEmpty()) &&
            (videoFile == null || videoFile.isEmpty())) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        try {
            String userRole = userDetails.getAuthorities().stream()
                                         .findFirst()
                                         .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                                         .orElseThrow(() -> new IllegalArgumentException("User role not found"));

            PostDTO createdPost = postService.createPostForUser(
                    userDetails.getUsername(),
                    postCreateDTO,
                    imageFile,
                    videoFile,
                    userRole
            );
            return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            System.err.println("Bad Request for user post: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            System.err.println("File upload error for user post: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during user post creation: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // NEW: Endpoint for the "Explore" feed
    // Accessed via GET http://localhost:8080/api/feed/explore
    @GetMapping("/explore")
    public ResponseEntity<List<PostDTO>> getExploreFeed() {
        List<PostDTO> explorePosts = postService.getExploreFeedPosts();
        return ResponseEntity.ok(explorePosts);
    }

    // NEW: Endpoint for the "For You" feed
    // Accessed via GET http://localhost:8080/api/feed/for-you
    // Requires authentication to get the user's interests
    @GetMapping("/for-you")
    @PreAuthorize("hasAnyRole('USER', 'OWNER')") // Only authenticated users (USER/OWNER) can get 'for you' feed
    public ResponseEntity<List<PostDTO>> getForYouFeed(@AuthenticationPrincipal MyUser currentUser) { // Directly inject MyUser
        if (currentUser == null) {
            // This case should ideally not happen if @PreAuthorize works correctly,
            // but good for explicit null check if principal isn't always MyUser
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        List<PostDTO> forYouPosts = postService.getForYouFeedPosts(currentUser);
        return ResponseEntity.ok(forYouPosts);
    }
}