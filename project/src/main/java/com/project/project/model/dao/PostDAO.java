package com.project.project.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // NEW: Import this

import com.project.project.model.Post;

public interface PostDAO extends JpaRepository<Post, Long> {

    // Finds all posts where the user's role matches the given role
    List<Post> findByUserRole(String role);

    // UPDATED: Added @Param and changed method name for clarity
    @Query("SELECT DISTINCT p FROM Post p JOIN p.tags t WHERE t IN :userInterests")
    List<Post> findByTagsInUserInterests(@Param("userInterests") List<String> userInterests);

}