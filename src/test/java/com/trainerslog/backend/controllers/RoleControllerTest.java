package com.trainerslog.backend.controllers;

import com.trainerslog.backend.entities.Role;
import com.trainerslog.backend.services.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class RoleControllerTest {
    @Autowired
    private RoleController roleController;

    @MockBean
    private RoleService roleService;

    private Role emptyRole;

    @BeforeEach
    void setUp() {
        emptyRole = new Role();
    }

    @Test
    void testCreateRole() {
        when(roleService.createRole(any())).thenReturn(emptyRole);
        assertEquals(ResponseEntity.status(HttpStatus.CREATED).build(), roleController.createRole(emptyRole));
        verify(roleService, times(1)).createRole(any());
    }
}
