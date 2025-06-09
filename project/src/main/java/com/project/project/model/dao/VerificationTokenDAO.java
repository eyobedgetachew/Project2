package com.project.project.model.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import com.project.project.model.MyUser;
import com.project.project.model.VerificationToken;
public interface  VerificationTokenDAO extends ListCrudRepository<VerificationToken,Long > {
    
 //Optional<VerificationToken> findByToken(String token); 
 @Query(value= "SELECT * FROM verification_tokens  WHERE token = CAST(:token AS TEXT)", nativeQuery = true) 
 Optional<VerificationToken> findByTokenNative(@Param("token")String token); 
 void deleteByUser(MyUser user);
}
