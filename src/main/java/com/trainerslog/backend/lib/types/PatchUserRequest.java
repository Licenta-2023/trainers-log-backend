package com.trainerslog.backend.lib.types;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record PatchUserRequest(

        String firstName,
        String lastName,
        @JsonFormat(pattern="dd-MM-yyyy") LocalDate dob
) {}
