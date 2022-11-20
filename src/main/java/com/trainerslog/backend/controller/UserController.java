package com.trainerslog.backend.controller;

import com.trainerslog.backend.lib.entity.User;
import com.trainerslog.backend.lib.types.UserRoleAdd;
import com.trainerslog.backend.lib.util.ResponseBuilder;
import com.trainerslog.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        this.userService.createUser(user);
        return ResponseBuilder.created();
    }

    @PostMapping("/addRole")
    public ResponseEntity<?> addRoleToUser(@Valid @RequestBody UserRoleAdd userRoleAdd) {
        this.userService.addRoleToUser(userRoleAdd.username(), userRoleAdd.roleName());
        return ResponseBuilder.ok(String.format("Successfully added role %s to %s", userRoleAdd.username(), userRoleAdd.roleName()));
    }

    @PostMapping("/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        userService.refreshToken(request, response);
    }
}
