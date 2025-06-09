package com.project.project.api.controller.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.project.api.model.LoginBody;
import com.project.project.api.model.LoginResponse;
import com.project.project.api.model.RegistrationBody;
import com.project.project.exception.AlreadyExistsException;
import com.project.project.exception.EmailFaliureException;
import com.project.project.exception.UserNotVerifiedException;
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
    public ResponseEntity register(@Valid @RequestBody  RegistrationBody registrationBody) {
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





 @GetMapping("/me")
public MyUser getLogggedInUserProfile(@AuthenticationPrincipal MyUser user) {
    return user;
}
}

