package com.project.project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.project.model.MyUser;
import com.project.project.model.Reservation;
import com.project.project.model.dao.ReservationDAO;

@Service
public class ReservationService {

    private ReservationDAO reservationDAO;
    public ReservationService(ReservationDAO reservationDAO) {
        this.reservationDAO = reservationDAO;
    }

    public List<Reservation> getReservations(MyUser user) {
        return reservationDAO.findByUser(user);
    }
}
