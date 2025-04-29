package com.project.project.api.controller.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.project.api.model.RegistrationBody;
import com.project.project.exception.AlreadyExistsException;
import com.project.project.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

private UserService userService;
public AuthController(UserService userService){
    this.userService = userService;
}
@PostMapping("/register")
    public ResponseEntity register(@RequestBody  RegistrationBody registrationBody) {
        try {
            userService.registerUser(registrationBody);
        return ResponseEntity.ok().build();
        } catch (AlreadyExistsException e) {
           
        }return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

}
