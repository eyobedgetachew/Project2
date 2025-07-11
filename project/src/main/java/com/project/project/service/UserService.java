package com.project.project.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.project.api.model.LoginBody;
import com.project.project.api.model.RegistrationBody;
import com.project.project.exception.AlreadyExistsException;
import com.project.project.model.MyUser;
import com.project.project.model.dao.MyUserDAO;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private PasswordEncoder passwordEncoder;
    private JWTService jwtService;
    private MyUserDAO myUserDAO;
    private final CloudinaryService cloudinaryService;

    public UserService(MyUserDAO myUserDAO, PasswordEncoder passwordEncoder, JWTService jwtService, CloudinaryService cloudinaryService) {
        this.myUserDAO = myUserDAO;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.cloudinaryService = cloudinaryService;
    }
    
    @Transactional
    public MyUser registerUser(RegistrationBody registrationBody) throws AlreadyExistsException {
        if (myUserDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()
                || myUserDAO.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()) {
            throw new AlreadyExistsException();
        }

        MyUser user = new MyUser();
        user.setEmail(registrationBody.getEmail());
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());
        user.setPassword(passwordEncoder.encode(registrationBody.getPassword())); 
        user.setUsername(registrationBody.getUsername());

        String requestedRole = registrationBody.getRole().toUpperCase();
        if (!requestedRole.equals("USER") && !requestedRole.equals("OWNER")) {
            throw new IllegalArgumentException("Invalid role: must be USER or OWNER");
        }
        user.setRole(requestedRole);

        if (registrationBody.getInterests() != null) {
            user.setInterests(registrationBody.getInterests());
        }

        user.setEmailVerified(true); 

        MyUser savedUser = myUserDAO.save(user);
        return savedUser;
    }

    public String generateJwtForUser(MyUser user) {
        return jwtService.generateJWt(user);
    }

    public String loginUser(LoginBody loginBody) {
        Optional<MyUser> opUser = myUserDAO.findByUsernameIgnoreCase(loginBody.getUsername());
        if (opUser.isPresent()) {
            MyUser user = opUser.get();
            if (passwordEncoder.matches(loginBody.getPassword(), user.getPassword())) {
                return jwtService.generateJWt(user);
            }
        }
        return null;
    }

    public MyUser updateProfilePicture(Long userId, MultipartFile profilePictureFile) throws IOException {
        Optional<MyUser> opUser = myUserDAO.findById(userId);
        if (opUser.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        MyUser user = opUser.get();

        String imageUrl = null;
        if (profilePictureFile != null && !profilePictureFile.isEmpty()) {
            imageUrl = cloudinaryService.uploadFile(profilePictureFile);
        } else {
            imageUrl = null; // Explicitly set to null if no file is provided/skipped
        }

        user.setProfilePictureUrl(imageUrl);
        return myUserDAO.save(user);
    }

    public MyUser removeProfilePicture(Long userId) {
        Optional<MyUser> opUser = myUserDAO.findById(userId);
        if (opUser.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        MyUser user = opUser.get();
        user.setProfilePictureUrl(null);
        return myUserDAO.save(user);
    }

    @Transactional
    public void markOnboardingComplete(Long userId) throws IllegalArgumentException {
        Optional<MyUser> opUser = myUserDAO.findById(userId);
        if (opUser.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        MyUser user = opUser.get();
        user.setOnboardingComplete(true);
        myUserDAO.save(user);
    }

    @Transactional
    public MyUser updateUserInterests(Long userId, List<String> newInterests) throws IllegalArgumentException {
        Optional<MyUser> opUser = myUserDAO.findById(userId);
        if (opUser.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        MyUser user = opUser.get();
        user.setInterests(newInterests);
        return myUserDAO.save(user);
    }

    // NEW: Method to update user's bio
    @Transactional
    public MyUser updateUserBio(Long userId, String bio) throws IllegalArgumentException {
        Optional<MyUser> opUser = myUserDAO.findById(userId);
        if (opUser.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        MyUser user = opUser.get();
        user.setBio(bio);
        return myUserDAO.save(user);
    }
}
