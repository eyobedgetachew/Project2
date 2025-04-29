package com.project.project.service;

import org.springframework.stereotype.Service;

import com.project.project.api.model.RegistrationBody;
import com.project.project.exception.AlreadyExistsException;
import com.project.project.model.MyUser;
import com.project.project.model.dao.MyUserDAO;

@Service
public class UserService {
    public UserService(MyUserDAO myUserDAO) {
        this.myUserDAO = myUserDAO;
    }
    private MyUserDAO myUserDAO;
public MyUser registerUser(RegistrationBody registrationBody) throws AlreadyExistsException{

    if (myUserDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()
     || myUserDAO.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()){
throw new AlreadyExistsException();}


MyUser user= new MyUser();
user.setEmail(registrationBody.getEmail());
user.setFirstName(registrationBody.getFirstName());
user.setLastName(registrationBody.getLastName());
user.setPassword(registrationBody.getPassword());
user.setUsername(registrationBody.getUsername());
user=myUserDAO.save(user);
return user;
}
}
