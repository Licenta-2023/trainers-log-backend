package com.trainerslog.backend.controllers;

import com.trainerslog.backend.lib.entities.User;
import com.trainerslog.backend.lib.types.UserRoleAdd;
import com.trainerslog.backend.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserControllerTest {
    @Autowired
    private UserController userController;

    @MockBean
    private UserService userService;

    private User emptyUser;

    @BeforeEach
    void setUp() {
        emptyUser = new User();
    }

    @Test
    void testGetUsers() {
        List<User> emptyUserList = Collections.emptyList();
        when(userService.getUsers()).thenReturn(emptyUserList);
        assertEquals(ResponseEntity.ok().body(emptyUserList), userController.getUsers());
        verify(userService, times(1)).getUsers();
    }

    @Test
    void testRegisterUser() {
        when(userService.createUser(any())).thenReturn(emptyUser);
        assertEquals(ResponseEntity.status(HttpStatus.CREATED).build(), userController.registerUser(emptyUser));
        verify(userService, times(1)).createUser(any());
    }

    @Disabled("Will be implemented in TLB-4")
    @Test
    void testLoginUser() {

    }

    @Test
    void testAddRoleToUser() {
        String username = "some-username";
        String roleName = "some-role";
        UserRoleAdd userRoleAdd = new UserRoleAdd(username, roleName);
        assertEquals(ResponseEntity.ok().body(String.format("Successfully added role %s to %s", userRoleAdd.userName(), userRoleAdd.roleName())), userController.addRoleToUser(userRoleAdd));
        verify(userService, times(1)).addRoleToUser(any(), any());
    }
}
