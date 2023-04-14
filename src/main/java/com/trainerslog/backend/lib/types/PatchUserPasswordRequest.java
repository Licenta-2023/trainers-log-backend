package com.trainerslog.backend.lib.types;

public record PatchUserPasswordRequest(
        String currentPassword,
        String newPassword
) {}
