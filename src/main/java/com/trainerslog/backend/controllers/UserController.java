package com.trainerslog.backend.controllers;

import com.trainerslog.backend.entities.User;
import com.trainerslog.backend.lib.types.UserLoginCredentials;
import com.trainerslog.backend.lib.types.UserRoleAdd;
import com.trainerslog.backend.lib.utils.ResponseBuilder;
import com.trainerslog.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<?> getUsers() {
        return ResponseBuilder.ok(this.userService.getUsers());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        this.userService.createUser(user);
        return ResponseBuilder.created();
    }

    //TODO: implement this in TLB-4 and also add tests
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginCredentials userLoginCredentials) {
        return ResponseBuilder.ok("Will be implemented with");
    }

    @PostMapping("/addRole")
    public ResponseEntity<?> addRoleToUser(@RequestBody UserRoleAdd userRoleAdd) {
        this.userService.addRoleToUser(userRoleAdd.userName(), userRoleAdd.roleName());
        return ResponseBuilder.ok(String.format("Successfully added role %s to %s", userRoleAdd.userName(), userRoleAdd.roleName()));
    }
}
