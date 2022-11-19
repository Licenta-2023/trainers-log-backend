package com.trainerslog.backend.controllers;

import com.trainerslog.backend.lib.entities.User;
import com.trainerslog.backend.lib.types.UserRoleAdd;
import com.trainerslog.backend.lib.utils.ResponseBuilder;
import com.trainerslog.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController()
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getUsers() {
        return ResponseBuilder.ok(this.userService.getUsers());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        this.userService.createUser(user);
        return ResponseBuilder.created();
    }

    @PostMapping("/addRole")
    public ResponseEntity<?> addRoleToUser(@RequestBody UserRoleAdd userRoleAdd) {
        this.userService.addRoleToUser(userRoleAdd.userName(), userRoleAdd.roleName());
        return ResponseBuilder.ok(String.format("Successfully added role %s to %s", userRoleAdd.userName(), userRoleAdd.roleName()));
    }

    @PostMapping("/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        userService.refreshToken(request, response);
    }
}
