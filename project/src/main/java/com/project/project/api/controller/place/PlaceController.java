package com.project.project.api.controller.place;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.project.model.Place;
import com.project.project.service.PlaceService;

@RestController
@RequestMapping("/api/places")
public class PlaceController {
 private PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

     @GetMapping
     public List<Place> getPlaces() {
         return placeService.getPlaces();
     }

}
