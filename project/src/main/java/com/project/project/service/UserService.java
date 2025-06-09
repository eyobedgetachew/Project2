package com.project.project.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

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
    public UserService(MyUserDAO myUserDAO, EncryptionService encryptionService, JWTService jwtService, EmailService emailService, VerificationTokenDAO verificationTokenDAO) {
        this.verificationTokenDAO = verificationTokenDAO;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.myUserDAO = myUserDAO;
        this.encryptionService = encryptionService;
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
         MyUser savedUser = myUserDAO.save(user);
         VerificationToken verificationToken = createVerificationToken(savedUser);
        verificationTokenDAO.save(verificationToken);
        emailService.sendEmail(verificationToken);
        return savedUser;
        //return myUserDAO.save(user);
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
}
