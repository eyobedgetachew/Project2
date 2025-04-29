package com.project.project.model.dao;


import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.project.project.model.MyUser;



public interface MyUserDAO extends CrudRepository<MyUser, Long> {
     Optional<MyUser> findByEmailIgnoreCase(String email);
       
     Optional<MyUser> findByUsernameIgnoreCase(String username) ;
       

}
