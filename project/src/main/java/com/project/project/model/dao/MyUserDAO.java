package com.project.project.model.dao;


import java.util.Optional;

import org.springframework.data.repository.ListCrudRepository;

import com.project.project.model.MyUser;



public interface MyUserDAO extends ListCrudRepository<MyUser, Long> {
     Optional<MyUser> findByEmailIgnoreCase(String email);
       
     Optional<MyUser> findByUsernameIgnoreCase(String username) ;
       

}
