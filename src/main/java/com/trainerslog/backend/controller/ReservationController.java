package com.trainerslog.backend.controller;

import com.trainerslog.backend.lib.types.ReservationRequest;
import com.trainerslog.backend.lib.util.ResponseBuilder;
import com.trainerslog.backend.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<?> addReservation(
            @RequestBody ReservationRequest reservationRequest
            ) {
        return ResponseBuilder.ok(reservationService.addReservationToTrainer(reservationRequest));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteReservation(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody ReservationRequest reservationRequest
    ) {
        reservationService.deleteReservation(reservationRequest, authorization);
        return ResponseBuilder.ok();
    }

    @GetMapping("/users/{username}/months/{month}")
    public ResponseEntity<?> getReservationsForUserByMonth(
            @PathVariable("username") String username,
            @PathVariable("month") Integer month
    ) {
       return ResponseBuilder.ok(reservationService.getCurrentMonthReservationsForUser(username, month));
    }

    @GetMapping("/users/{username}/months/{month}/days/{day}")
    public ResponseEntity<?> getReservationsForUserByMonthAndDay(
            @PathVariable("username") String username,
            @PathVariable("month") Integer month,
            @PathVariable("day") Integer day
    ) {
       return ResponseBuilder.ok(reservationService.getCurrentDayReservationsForUser(username, month, day));
    }

    @GetMapping("/trainers/{username}/months/{month}")
    public ResponseEntity<?> getReservationsForTrainerByMonth(
            @PathVariable("username") String username,
            @PathVariable("month") Integer month
    ) {
        return ResponseBuilder.ok(reservationService.getCurrentMonthReservationsForTrainer(username, month));
    }

    @GetMapping("/trainers/{username}/months/{month}/days/{day}")
    public ResponseEntity<?> getReservationsForTrainerByMonthAndDay(
            @PathVariable("username") String username,
            @PathVariable("month") Integer month,
            @PathVariable("day") Integer day
    ) {
        return ResponseBuilder.ok(reservationService.getCurrentDayReservationsForTrainer(username, month, day));
    }
}
