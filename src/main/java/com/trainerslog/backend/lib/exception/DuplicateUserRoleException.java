package com.trainerslog.backend.lib.exception;

public class DuplicateUserRoleException extends RuntimeException{
    public DuplicateUserRoleException(String message) {
        super(message);
    }
}
