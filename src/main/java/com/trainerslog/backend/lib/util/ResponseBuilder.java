package com.trainerslog.backend.lib.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseBuilder {
    public static ResponseEntity<?> ok(Object body) {
        return ResponseEntity.ok().body(body);
    }

    public static ResponseEntity<?> ok() {
        return ResponseEntity.ok().build();
    }

    public static ResponseEntity<?> created() {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public static ResponseEntity<?> conflict(Object body) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    public static ResponseEntity<?> notFound(Object body) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    public static ResponseEntity<?> badRequest(Object body) {
        return ResponseEntity.badRequest().body(body);
    }
}
