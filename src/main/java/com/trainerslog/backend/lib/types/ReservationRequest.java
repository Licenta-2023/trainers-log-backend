package com.trainerslog.backend.lib.types;

import java.time.LocalDateTime;

public record ReservationRequest(
        String username,
        String trainerUsername,
        LocalDateTime timeIntervalBegin,
        ReservationType reservationType
){}
