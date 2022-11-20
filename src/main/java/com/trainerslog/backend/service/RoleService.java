package com.trainerslog.backend.service;

import com.trainerslog.backend.lib.entity.Role;
import com.trainerslog.backend.lib.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }
}
