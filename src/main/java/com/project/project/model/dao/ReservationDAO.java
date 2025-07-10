package com.project.project.model.dao;

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;

import com.project.project.model.MyUser;
import com.project.project.model.Reservation;

public interface  ReservationDAO extends ListCrudRepository<Reservation, Long> {

    
    List<Reservation> findByUser(MyUser user);

    
    //List<Reservation> findByPlaceId(Long placeId);

   
    //List<Reservation> findByDate(String date);

    
    //List<Reservation> findByTime(String time);

}
