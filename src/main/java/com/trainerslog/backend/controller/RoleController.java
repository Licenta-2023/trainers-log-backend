package com.trainerslog.backend.controller;

import com.trainerslog.backend.lib.entity.Role;
import com.trainerslog.backend.lib.util.ResponseBuilder;
import com.trainerslog.backend.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController()
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping()
    public ResponseEntity<?> createRole(@Valid @RequestBody Role role) {
        this.roleService.createRole(role);
        return ResponseBuilder.created();
    }
}
