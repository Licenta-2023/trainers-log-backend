package com.trainerslog.backend.controllers;

import com.trainerslog.backend.lib.entities.Role;
import com.trainerslog.backend.lib.utils.ResponseBuilder;
import com.trainerslog.backend.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping()
    public ResponseEntity<?> createRole(@RequestBody Role role) {
        this.roleService.createRole(role);
        return ResponseBuilder.created();
    }
}
