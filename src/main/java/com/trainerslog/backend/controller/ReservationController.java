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
}
