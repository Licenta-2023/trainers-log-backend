package com.trainerslog.backend.exception;

public class DuplicateUserRoleException extends RuntimeException{
    public DuplicateUserRoleException(String message) {
        super(message);
    }
}
