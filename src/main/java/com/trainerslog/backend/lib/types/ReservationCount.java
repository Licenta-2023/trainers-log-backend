package com.trainerslog.backend.lib.types;

import java.util.Date;

public record ReservationCount(
        Date date,
        Long count
) {}
