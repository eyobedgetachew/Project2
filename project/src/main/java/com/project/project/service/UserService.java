package com.project.project.service;

import java.io.IOException; // NEW: Import for IOException
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile; // NEW: Import for MultipartFile

import com.project.project.api.model.LoginBody;
import com.project.project.api.model.RegistrationBody;
import com.project.project.exception.AlreadyExistsException;
import com.project.project.exception.EmailFaliureException;
import com.project.project.exception.UserNotVerifiedException;
import com.project.project.model.MyUser;
import com.project.project.model.VerificationToken;
import com.project.project.model.dao.MyUserDAO;
import com.project.project.model.dao.VerificationTokenDAO;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private EncryptionService encryptionService;
    private JWTService jwtService;
    private EmailService emailService;
    private VerificationTokenDAO verificationTokenDAO;
    private MyUserDAO myUserDAO;
    private final CloudinaryService cloudinaryService; // NEW: Declare CloudinaryService

    // UPDATED: Constructor to inject CloudinaryService
    public UserService(MyUserDAO myUserDAO, EncryptionService encryptionService, JWTService jwtService, EmailService emailService, VerificationTokenDAO verificationTokenDAO, CloudinaryService cloudinaryService) {
        this.verificationTokenDAO = verificationTokenDAO;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.myUserDAO = myUserDAO;
        this.encryptionService = encryptionService;
        this.cloudinaryService = cloudinaryService; // NEW: Initialize CloudinaryService
    }
    
    public MyUser registerUser(RegistrationBody registrationBody) throws AlreadyExistsException, EmailFaliureException {
        if (myUserDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()
                || myUserDAO.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()) {
            throw new AlreadyExistsException();
        }

        MyUser user = new MyUser();
        user.setEmail(registrationBody.getEmail());
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());
        user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));
        user.setUsername(registrationBody.getUsername());

        String requestedRole = registrationBody.getRole().toUpperCase();
        if (!requestedRole.equals("USER") && !requestedRole.equals("OWNER")) {
            throw new IllegalArgumentException("Invalid role: must be USER or OWNER");
        }
        user.setRole(requestedRole);

        if (registrationBody.getInterests() != null) {
            user.setInterests(registrationBody.getInterests());
        }

        MyUser savedUser = myUserDAO.save(user);
        VerificationToken verificationToken = createVerificationToken(savedUser);
        verificationTokenDAO.save(verificationToken);
        emailService.sendEmail(verificationToken);
        return savedUser;
    }

    private VerificationToken createVerificationToken(MyUser user) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtService.generateVerificationJWT(user));
        verificationToken.setExpiryDate(Timestamp.valueOf(java.time.LocalDateTime.now().plusDays(1)));
        verificationToken.setCreatedTime(new Timestamp(System.currentTimeMillis()));
        verificationToken.setUser(user);
        user.getVerificationTokens().add(verificationToken);
        return verificationToken;
    }

    public String loginUser(LoginBody loginBody) throws UserNotVerifiedException, EmailFaliureException {
        Optional<MyUser> opUser = myUserDAO.findByUsernameIgnoreCase(loginBody.getUsername());
        if (opUser.isPresent()) {
            MyUser user = opUser.get();
            if (encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())) {
                if (user.isEmailVerified()){
                    return jwtService.generateJWt(user);
                } else {
                    List<VerificationToken> verificationTokens = user.getVerificationTokens();
                    boolean resend = verificationTokens.isEmpty() ||
                            verificationTokens.get(0).getCreatedTime().before(new Timestamp(System.currentTimeMillis() - (60 * 60 * 1000)));
                    if(resend){
                        VerificationToken verificationToken = createVerificationToken(user);
                        verificationTokenDAO.save(verificationToken);
                        emailService.sendEmail(verificationToken);
                    }
                    throw new UserNotVerifiedException(resend);
                }
            }
        }
        return null;
    }

    @Transactional
    public boolean verifyUser(String token) {
        Optional<VerificationToken> opToken = verificationTokenDAO.findByTokenNative(token);

        if (opToken.isPresent()) {
            VerificationToken verificationToken = opToken.get();
            MyUser user = verificationToken.getUser();

            if (user.isEmailVerified()) {
                return false; 
            }

            if (verificationToken.getExpiryDate().before(new Timestamp(System.currentTimeMillis()))) {
                verificationTokenDAO.deleteByUser(user); 
                return false; 
            }
            
            user.setEmailVerified(true);
            myUserDAO.save(user);
            verificationTokenDAO.deleteByUser(user); 
            return true; 
        } else {
        }
        return false; 
    }

    // NEW: Method to update profile picture
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
            // If no file is provided, you might want to clear an existing profile picture
            // Or simply do nothing, depending on your desired behavior
            imageUrl = null; // Set to null if the goal is to remove it when no new file is provided
        }

        user.setProfilePictureUrl(imageUrl);
        return myUserDAO.save(user);
    }

    // NEW: Method to remove profile picture
    public MyUser removeProfilePicture(Long userId) {
        Optional<MyUser> opUser = myUserDAO.findById(userId);
        if (opUser.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        MyUser user = opUser.get();
        user.setProfilePictureUrl(null); // Set URL to null to remove
        return myUserDAO.save(user);
    }
}