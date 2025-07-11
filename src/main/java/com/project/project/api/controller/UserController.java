package com.project.project.api.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PatchMapping; // NEW: Import PatchMapping
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.project.api.DTO.UserBioDTO; // NEW: Import UserBioDTO
import com.project.project.model.MyUser;
import com.project.project.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint to update user interests
    @PostMapping("/me/interests")
    public ResponseEntity<Map<String, String>> updateInterests(
            @AuthenticationPrincipal MyUser currentUser,
            @RequestBody Map<String, List<String>> payload) {
        try {
            List<String> interests = payload.get("interests");
            if (interests == null) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Interests list is missing in the request body."));
            }
            userService.updateUserInterests(currentUser.getId(), interests);
            return ResponseEntity.ok(Collections.singletonMap("message", "Interests updated successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error updating interests: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    // Endpoint to mark onboarding as complete
    @PostMapping("/me/complete-onboarding")
    public ResponseEntity<Map<String, String>> completeOnboarding(@AuthenticationPrincipal MyUser currentUser) {
        try {
            System.out.println("Attempting to complete onboarding for user ID: " + currentUser.getId() + ", username: " + currentUser.getUsername());
            userService.markOnboardingComplete(currentUser.getId());
            return ResponseEntity.ok(Collections.singletonMap("message", "Onboarding marked as complete."));
        } catch (IllegalArgumentException e) {
            System.err.println("IllegalArgumentException in completeOnboarding: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Generic error in completeOnboarding: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "An unexpected error occurred."));
        }
    }

    // NEW: Endpoint to update user's bio
    @PatchMapping("/me/bio") // Using PATCH for partial update of user profile
    public ResponseEntity<Map<String, String>> updateUserBio(
            @AuthenticationPrincipal MyUser currentUser,
            @RequestBody UserBioDTO bioDTO) {
        try {
            userService.updateUserBio(currentUser.getId(), bioDTO.getBio());
            return ResponseEntity.ok(Collections.singletonMap("message", "User bio updated successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error updating user bio: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "An unexpected error occurred while updating bio."));
        }
    }

    // Global exception handler for this controller
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception e) {
        System.err.println("Caught exception in UserController @ExceptionHandler: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(Collections.singletonMap("message", "An internal server error occurred: " + e.getMessage()));
    }
}
