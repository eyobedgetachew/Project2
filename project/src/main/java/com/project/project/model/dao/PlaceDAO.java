package com.project.project.model.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.project.model.MyUser;
import com.project.project.model.Place;

@Repository
public interface PlaceDAO extends JpaRepository<Place, Long> {
    // Find a place by its owner.
    // Assuming an owner can only have one place. If multiple, return List<Place>.
    Optional<Place> findByOwner(MyUser owner);
}
