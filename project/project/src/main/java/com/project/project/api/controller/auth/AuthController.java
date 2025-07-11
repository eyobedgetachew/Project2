package com.project.project.api.controller.auth;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // NEW Import
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController; // NEW Import
import org.springframework.web.multipart.MultipartFile;

import com.project.project.api.model.LoginBody;
import com.project.project.api.model.LoginResponse;
import com.project.project.api.model.RegistrationBody;
import com.project.project.exception.AlreadyExistsException;
import com.project.project.exception.EmailFaliureException;
import com.project.project.exception.UserNotVerifiedException;
import com.project.project.model.MyUser;
import com.project.project.service.UserService;

import jakarta.validation.Valid; // NEW Import for handling file upload exceptions

@RestController
@RequestMapping("/auth") // The base path for this controller
public class AuthController {

    private UserService userService;

    public AuthController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody RegistrationBody registrationBody) {
        try {
            userService.registerUser(registrationBody);
            return ResponseEntity.ok().build();
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }catch (EmailFaliureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity <LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) {
        String jwt = null;
        try { jwt = userService.loginUser(loginBody);}
        catch(UserNotVerifiedException e){
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            String reason = "USER_NOT_VERIFIED";
            if (e.isNewEmailSent()) {
                reason += "EMAIL_RESENT";
            }
            response.setFailReason(reason);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        catch(EmailFaliureException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            } 
        else {
            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        if (userService.verifyUser(token)){
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    // Existing endpoint for fetching user profile
    @GetMapping("/me")
    public MyUser getLogggedInUserProfile(@AuthenticationPrincipal MyUser user) {
        return user;
    }

    // NEW: Endpoint for uploading profile picture
    // This will be accessed via POST http://localhost:8080/auth/profile-picture
    @PostMapping("/profile-picture")
    public ResponseEntity<?> uploadProfilePicture(
            @AuthenticationPrincipal MyUser currentUser, // Automatically injects the authenticated user
            @RequestParam("profilePictureFile") MultipartFile profilePictureFile) {
        try {
            // Delegate to UserService to handle the upload and user update
            MyUser updatedUser = userService.updateProfilePicture(currentUser.getId(), profilePictureFile);
            return ResponseEntity.ok("Profile picture uploaded successfully for user: " + updatedUser.getUsername());
        } catch (IllegalArgumentException e) {
            // Handle cases where user is not found (though @AuthenticationPrincipal should prevent this)
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            // Handle file upload specific errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload profile picture: " + e.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            e.printStackTrace(); // Print stack trace for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    // NEW: Endpoint for removing profile picture
    // This will be accessed via DELETE http://localhost:8080/auth/profile-picture
    @DeleteMapping("/profile-picture")
    public ResponseEntity<?> removeProfilePicture(@AuthenticationPrincipal MyUser currentUser) {
        try {
            // Delegate to UserService to remove the profile picture
            MyUser updatedUser = userService.removeProfilePicture(currentUser.getId());
            return ResponseEntity.ok("Profile picture removed successfully for user: " + updatedUser.getUsername());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }
}