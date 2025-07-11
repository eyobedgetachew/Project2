package com.project.project.api.model;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegistrationBody {
   
    @NotNull
    @NotBlank
    @Size(min = 3,max = 69, message = "User name must be between 3-69 characters")
   
    private String username;
   
    @NotNull
    @NotBlank
   @Size(min = 8,max = 69, message = "Password must be between 8-69 characters")
    // Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit.
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$",
             message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit.")
    private String password;
   
    @NotNull
    @NotBlank
    @Pattern(
    regexp = "^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}$",
    message = "Invalid email address")
    @Email
    private String email;

    @NotNull
    @NotBlank
    private String firstname;
   
    @NotNull
    @NotBlank
    private String lastname;




    @NotBlank(message = "Role is required")
    private String role;
    private List<String> interests;
    
    
    
    
    public List<String> getInterests() {return interests;}
    public void setInterests(List<String> interests) {this.interests = interests;}
    
    
public String getRole() {return role;}
public void setRole(String role) {this.role = role;}
public String getUsername(){return username;}
public void setUsername(String username){this.username=username;}
public String getPassword(){return password;}
public void setPassword(String password){this.password=password;}
public String getEmail(){return email;}
public void setEmail(String email){this.email=email;}
public String getFirstName(){return firstname;}
public void setFirstName(String firstname){this.firstname= firstname;}
public String getLastName(){return lastname;}
public void setLastName (String lastname){this.lastname=lastname;}

}
