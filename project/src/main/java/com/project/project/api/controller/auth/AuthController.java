package com.project.project.api.controller.auth;

import java.io.IOException;
import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.project.api.DTO.UserProfileDTO;
import com.project.project.api.model.LoginBody;
import com.project.project.api.model.LoginResponse;
import com.project.project.api.model.RegistrationBody;
import com.project.project.exception.AlreadyExistsException; // IMPORTANT: Ensure this import is present
import com.project.project.model.MyUser;
import com.project.project.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private UserService userService;

    public AuthController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegistrationBody registrationBody) {
        try {
            MyUser registeredUser = userService.registerUser(registrationBody); 
            String jwt = userService.generateJwtForUser(registeredUser); 
            
            LoginResponse response = new LoginResponse();
            response.setSuccess(true);
            response.setJwt(jwt);
            response.setFailReason(null);
            return ResponseEntity.ok(response);

        } catch (AlreadyExistsException e) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            response.setFailReason("USER_ALREADY_EXISTS");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            System.err.println("Unexpected error during registration: " + e.getMessage());
            e.printStackTrace();
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            response.setFailReason("INTERNAL_SERVER_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity <LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) {
        String jwt = null;
        try { 
            jwt = userService.loginUser(loginBody);
        } catch(Exception e) {
             System.err.println("Error during login attempt: " + e.getMessage());
             e.printStackTrace();
             LoginResponse response = new LoginResponse();
             response.setSuccess(false);
             response.setFailReason("INTERNAL_SERVER_ERROR");
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        if (jwt == null) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            response.setFailReason("INVALID_CREDENTIALS");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } 
        else {
            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/me")
    // IMPORTANT: Changed return type to UserProfileDTO
    public UserProfileDTO getLogggedInUserProfile(@AuthenticationPrincipal MyUser user) {
        // Map MyUser entity to UserProfileDTO to avoid lazy loading issues
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        // IMPORTANT: Call getInterests() now that the field name is plural
        dto.setInterests(user.getInterests()); 
        dto.setBio(user.getBio()); // Map bio field

        // If you had postsCount/likedPostsCount, you would set them here too:
        // dto.setPostsCount(user.getPosts().size()); // Example if posts were a collection
        // dto.setLikedPostsCount(user.getLikedPosts().size()); // Example if liked posts were a collection

        return dto;
    }

    @PostMapping("/profile-picture")
    public ResponseEntity<?> uploadProfilePicture(
            @AuthenticationPrincipal MyUser currentUser,
            @RequestParam("profilePictureFile") MultipartFile profilePictureFile) {
        try {
            MyUser updatedUser = userService.updateProfilePicture(currentUser.getId(), profilePictureFile);
            return ResponseEntity.ok(Collections.singletonMap("message", "Profile picture uploaded successfully for user: " + updatedUser.getUsername()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Failed to upload profile picture: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "An unexpected error occurred during picture upload: " + e.getMessage()));
        }
    }

    @DeleteMapping("/profile-picture")
    public ResponseEntity<?> removeProfilePicture(@AuthenticationPrincipal MyUser currentUser) {
        try {
            MyUser updatedUser = userService.removeProfilePicture(currentUser.getId());
            return ResponseEntity.ok(Collections.singletonMap("message", "Profile picture removed successfully for user: " + updatedUser.getUsername()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "An unexpected error occurred during picture removal: " + e.getMessage()));
        }
    }
}
