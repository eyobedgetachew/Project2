package com.project.project.model.dao;

import org.springframework.data.jpa.repository.JpaRepository; // Make sure to import your Menu entity
import org.springframework.stereotype.Repository;

import com.project.project.model.Menu;

@Repository
public interface MenuDAO extends JpaRepository<Menu, Long> {
    // Spring Data JPA will automatically provide common CRUD operations for the Menu entity
    // You don't need to write any methods here unless you need custom queries.
}