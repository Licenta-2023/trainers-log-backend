package com.trainerslog.backend.lib.utils;

import com.trainerslog.backend.entities.Role;
import com.trainerslog.backend.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResponseBuilderTest {
    @ParameterizedTest
    @MethodSource("generateTestObjects")
    void testOkWithString(Object body) {
        assertEquals(ResponseEntity.ok().body(body), ResponseBuilder.ok(body));
    }

    @Test
    void testCreated() {
        assertEquals(ResponseEntity.status(HttpStatus.CREATED).build(), ResponseBuilder.created());
    }


    private static Stream<Object> generateTestObjects() {
        return Stream.of(
                "some-string",
                new User(),
                new Role()
        );
    }
}
