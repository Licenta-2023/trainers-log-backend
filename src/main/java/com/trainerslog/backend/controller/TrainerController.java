package com.trainerslog.backend.controller;

import com.trainerslog.backend.lib.types.TrainerPresence;
import com.trainerslog.backend.lib.util.ResponseBuilder;
import com.trainerslog.backend.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/api/trainer")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @PostMapping("/presence/{trainerUsername}")
    public ResponseEntity<?> addPresenceToTrainer(
            @RequestBody TrainerPresence trainerPresence,
            @PathVariable("trainerUsername") String trainerUsername
    ) {
        return ResponseBuilder.ok(trainerService.addPresence(trainerUsername, trainerPresence));
    }

    @PostMapping("/clients/{trainerUsername}")
    public ResponseEntity<?> changeTrainerTotalClientsPerReservation(
            @PathVariable("trainerUsername") String trainerUsername,
            @RequestParam
            @Min(value = 1, message = "Min value is 1")
            @Max(value = 4, message = "Max value is 4")
            Integer totalClients
    ) {
        trainerService.setTotalClientsPerSessionForTrainer(trainerUsername, totalClients);
        return ResponseBuilder.ok();
    }

    @GetMapping("/usernames")
    public ResponseEntity<?> getAllUsernamesForTrainers() {
        return ResponseBuilder.ok(trainerService.getAllUsernamesForTrainers());
    }
}
