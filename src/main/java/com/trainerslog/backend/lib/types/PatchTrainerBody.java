package com.trainerslog.backend.lib.types;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;

public record PatchTrainerBody(
        @JsonFormat(pattern="HH:mm:ss") LocalTime startOfDay,
        @JsonFormat(pattern="HH:mm:ss") LocalTime endOfDay,
        Integer totalClientsPerReservation
) {}
