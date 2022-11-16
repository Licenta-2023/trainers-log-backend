package com.trainerslog.backend.lib.utils;

import org.springframework.http.ResponseEntity;

public class ResponseBuilder {
    public static ResponseEntity<?> ok() {
        return ResponseEntity.ok().build();
    }

    public static ResponseEntity<?> okWithBody(Object body) {
        return ResponseEntity.ok().body(body);
    }

    public static ResponseEntity<?> created() {
        return ResponseEntity.created(null).build();
    }
}
