package com.project.project.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.project.model.MyUser;
import com.project.project.model.Post; // Import MyUser for findByUser method

public interface PostDAO extends JpaRepository<Post, Long> {

    // Finds all posts where the user's role matches the given role
    List<Post> findByUserRole(String role);

    // Finds posts by tags in user interests
    @Query("SELECT DISTINCT p FROM Post p JOIN p.tags t WHERE t IN :userInterests")
    List<Post> findByTagsInUserInterests(@Param("userInterests") List<String> userInterests);

    // NEW: Find all posts by a specific user, ordered by creation time descending
    List<Post> findByUserOrderByCreatedAtDesc(MyUser user);

    // NEW: Find all posts (for a general feed), ordered by creation time descending
    List<Post> findAllByOrderByCreatedAtDesc();
}
