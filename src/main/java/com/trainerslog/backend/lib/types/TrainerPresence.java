package com.trainerslog.backend.lib.types;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public record TrainerPresence (
        LocalTime startOfDay,
        LocalTime endOfDay
){
    public TrainerPresence truncatedToHours() {
        return new TrainerPresence(startOfDay.truncatedTo(ChronoUnit.HOURS), endOfDay.truncatedTo(ChronoUnit.HOURS));
    }
}
