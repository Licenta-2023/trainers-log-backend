package com.trainerslog.backend.lib.types;

import javax.validation.constraints.NotBlank;

public record UserRoleAdd (
        @NotBlank(message = "Username cannot be empty")
        String username,
        @NotBlank(message = "Role name cannot be empty")
        String roleName
) {}
