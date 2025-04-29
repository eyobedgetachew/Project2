package com.project.project.api.model;

public class RegistrationBody {
    
    private String username;
    private String password;
    private String email;
    private String firstname;
    private String lastname;

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
