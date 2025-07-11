package com.project.project.api.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.project.api.DTO.PostCreateDTO;
import com.project.project.api.DTO.PostDTO;
import com.project.project.model.MyUser;
import com.project.project.service.PostService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // Endpoint for users to create a post (including image/video upload)
    // This will be used by both USER and OWNER roles
    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createPost(
            @AuthenticationPrincipal MyUser currentUser,
            @RequestPart("postData") @Valid PostCreateDTO postCreateDTO,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestPart(value = "videoFile", required = false) MultipartFile videoFile) {
        try {
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "User not authenticated."));
            }
            
            // The service method already handles role validation based on expectedRole
            // We pass the current user's role to the service
            PostDTO createdPost = postService.createPostForUser(
                currentUser.getUsername(), postCreateDTO, imageFile, videoFile, currentUser.getRole());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Failed to upload file: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error creating post: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "An unexpected error occurred."));
        }
    }

    // Endpoint to get all posts (Explore Feed)
    @GetMapping("/feed/explore")
    public ResponseEntity<List<PostDTO>> getExploreFeed() {
        List<PostDTO> posts = postService.getExploreFeedPosts();
        return ResponseEntity.ok(posts);
    }

    // Endpoint to get posts filtered by user interests (For You Feed)
    @GetMapping("/feed/for-you")
    public ResponseEntity<List<PostDTO>> getForYouFeed(@AuthenticationPrincipal MyUser currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<PostDTO> posts = postService.getForYouFeedPosts(currentUser);
        return ResponseEntity.ok(posts);
    }

    // Endpoint to get posts by the authenticated user (for their profile page)
    @GetMapping("/me")
    public ResponseEntity<List<PostDTO>> getMyPosts(@AuthenticationPrincipal MyUser currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<PostDTO> posts = postService.getPostsByUser(currentUser);
        return ResponseEntity.ok(posts);
    }

    // Optional: Get posts by a specific user ID (if you want public user profiles)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDTO>> getPostsByUserId(@PathVariable Long userId) {
        // You would need to fetch the MyUser object by ID first
        // Optional<MyUser> userOpt = myUserDAO.findById(userId);
        // if (userOpt.isEmpty()) { return ResponseEntity.notFound().build(); }
        // List<PostDTO> posts = postService.getPostsByUser(userOpt.get());
        // return ResponseEntity.ok(posts);
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); // Placeholder
    }
}
