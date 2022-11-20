package com.trainerslog.backend.service;

import com.trainerslog.backend.lib.entity.Role;
import com.trainerslog.backend.lib.repositories.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class RoleServiceTest {
    @Autowired
    private RoleService roleService;

    @MockBean
    private RoleRepository roleRepository;

    private Role emptyRole;

    @BeforeEach
    void setUp() {
        emptyRole = new Role();
    }

    @Test
    void testCreateRole() {
        when(roleRepository.save(any())).thenReturn(emptyRole);
        assertEquals(emptyRole, roleService.createRole(emptyRole));
        verify(roleRepository, times(1)).save(any());
    }
}
