package com.trainerslog.backend.lib.exception;

public class ReservationExistsException extends RuntimeException{
    public ReservationExistsException(String message) {
        super(message);
    }
}
