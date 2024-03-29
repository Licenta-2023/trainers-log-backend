package com.trainerslog.backend.lib.types;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public record PatchUserRequest(

        String firstName,
        String lastName,
        @JsonFormat(pattern="dd-MM-yyyy") LocalDate dob,
        List<UserRoles> newRoles,
        Optional<PatchTrainerBody> patchTrainerBody
) {}
