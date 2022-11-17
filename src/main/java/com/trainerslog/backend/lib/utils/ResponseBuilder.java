package com.trainerslog.backend.lib.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseBuilder {
    public static ResponseEntity<?> ok(Object body) {
        return ResponseEntity.ok().body(body);
    }

    public static ResponseEntity<?> created() {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
