package com.project.project.model.dao;

import java.util.List; // Import MyUser entity

import org.springframework.data.repository.ListCrudRepository;

import com.project.project.model.MyUser;
import com.project.project.model.Place; // Import List

public interface PlaceDAO extends ListCrudRepository<Place, Long> {

    // NEW: Find all places owned by a specific user
    List<Place> findByOwner(MyUser owner);

    // Alternative/convenience: Find all places by the owner's username
    // List<Place> findByOwnerUsername(String username); // This also works due to property traversal
}