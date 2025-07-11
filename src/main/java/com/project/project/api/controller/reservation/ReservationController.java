package com.project.project.api.controller.reservation;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.project.model.MyUser;
import com.project.project.model.Reservation;
import com.project.project.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
private ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    
     @GetMapping
    public List<Reservation> getReservations(@AuthenticationPrincipal MyUser user) {
         
        return reservationService.getReservations(user);
     }
}
