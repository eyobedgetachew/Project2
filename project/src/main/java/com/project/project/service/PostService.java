package com.project.project.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.project.api.DTO.PostCreateDTO;
import com.project.project.api.DTO.PostDTO; // Correct DTO package
import com.project.project.model.MyUser;
import com.project.project.model.Place;
import com.project.project.model.Post;
import com.project.project.model.dao.MyUserDAO;
import com.project.project.model.dao.PlaceDAO;
import com.project.project.model.dao.PostDAO;

@Service
public class PostService {

    private final PostDAO postDAO;
    private final MyUserDAO myUserDAO;
    private final PlaceDAO placeDAO;
    private final CloudinaryService cloudinaryService;

    public PostService(PostDAO postDAO, MyUserDAO myUserDAO, PlaceDAO placeDAO, CloudinaryService cloudinaryService) {
        this.postDAO = postDAO;
        this.myUserDAO = myUserDAO;
        this.placeDAO = placeDAO;
        this.cloudinaryService = cloudinaryService;
    }

    @Transactional
    public PostDTO createPostForUser(String username, PostCreateDTO dto,
                                     MultipartFile imageFile, MultipartFile videoFile, String expectedRole) throws IOException {

        Optional<MyUser> userOpt = myUserDAO.findByUsernameIgnoreCase(username);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        MyUser user = userOpt.get();

        if (!user.getRole().equalsIgnoreCase(expectedRole)) {
            throw new IllegalArgumentException("User role mismatch");
        }

        Post post = new Post();
        post.setUser(user);
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setCreatedAt(LocalDateTime.now());
        post.setTags(dto.getTags() != null ? new ArrayList<>(dto.getTags()) : new ArrayList<>());

        if ("OWNER".equalsIgnoreCase(expectedRole)) {
            if (dto.getPlaceId() == null) {
                throw new IllegalArgumentException("Place ID must be provided for owner posts");
            }
            Optional<Place> placeOpt = placeDAO.findById(dto.getPlaceId());
            if (placeOpt.isEmpty()) {
                throw new IllegalArgumentException("Place not found");
            }
            post.setPlace(placeOpt.get());
        } else {
            if (dto.getPlaceId() != null) {
                Optional<Place> placeOpt = placeDAO.findById(dto.getPlaceId());
                if (placeOpt.isEmpty()) {
                    throw new IllegalArgumentException("Place not found for user post with ID: " + dto.getPlaceId());
                }
                post.setPlace(placeOpt.get());
            }
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(imageFile);
            post.setImageUrl(imageUrl);
        }
        if (videoFile != null && !videoFile.isEmpty()) {
            String videoUrl = cloudinaryService.uploadFile(videoFile);
            post.setVideoUrl(videoUrl);
        }

        return toDTO(postDAO.save(post));
    }

    /**
     * Method for the "Explore" feed (all posts).
     * Renamed from getAllPosts for clarity in feed context.
     */
    @Transactional(readOnly = true)
    public List<PostDTO> getExploreFeedPosts() { // RENAMED for consistency
        return postDAO.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // You can keep getPostsByRole if you use it elsewhere, but it's not part of the feed feature
    @Transactional(readOnly = true)
    public List<PostDTO> getPostsByRole(String role) {
        return postDAO.findByUserRole(role)
                       .stream()
                       .map(this::toDTO)
                       .collect(Collectors.toList());
    }


    /**
     * Method for the "For You" feed (posts filtered by user interests/tags).
     * Takes MyUser directly and calls the new DAO method.
     */
    @Transactional(readOnly = true)
    public List<PostDTO> getForYouFeedPosts(MyUser user) { // UPDATED: Takes MyUser object
        if (user == null || user.getInterests() == null || user.getInterests().isEmpty()) {
            return List.of(); // Return empty list if no interests
        }
        List<String> interests = user.getInterests();
        // UPDATED: Call the correct DAO method name
        List<Post> posts = postDAO.findByTagsInUserInterests(interests);
        return posts.stream().map(this::toDTO).collect(Collectors.toList());
    }


    // --- UPDATED DTO Mapper ---
    private PostDTO toDTO(Post post) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setImageUrl(post.getImageUrl());
        dto.setVideoUrl(post.getVideoUrl());

        // Handle tags
        if (post.getTags() != null) {
            dto.setTags(new ArrayList<>(post.getTags()));
        } else {
            dto.setTags(new ArrayList<>());
        }

        // Set User details
        if (post.getUser() != null) {
            dto.setUsername(post.getUser().getUsername());
            // NEW: Map user's profile picture URL
            dto.setUserProfilePictureUrl(post.getUser().getProfilePictureUrl());
        }

        // Set Place details
        if (post.getPlace() != null) {
            dto.setPlaceId(post.getPlace().getId());
            dto.setPlaceName(post.getPlace().getName());
        }

        return dto;
    }
}