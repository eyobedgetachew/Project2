package com.project.project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.project.model.Place;
import com.project.project.model.dao.PlaceDAO;

@Service
public class PlaceService {
    private PlaceDAO placeDAO;
    public PlaceService(PlaceDAO placeDAO) {
        this.placeDAO = placeDAO;
    }
    public List<Place> getPlaces() {
        return placeDAO.findAll();
    }

}
