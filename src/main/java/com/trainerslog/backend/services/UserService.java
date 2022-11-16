package com.trainerslog.backend.services;

import com.trainerslog.backend.entities.Role;
import com.trainerslog.backend.entities.User;
import com.trainerslog.backend.exception.DuplicateUserRoleException;
import com.trainerslog.backend.exception.NotFoundException;
import com.trainerslog.backend.lib.types.UserRoles;
import com.trainerslog.backend.repositories.RoleRepository;
import com.trainerslog.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    public User createUser(User user) {
        log.info("Creating user {}.", user.getUsername());
        return userRepository.save(user);
    }

    public Collection<User> getAllUsers() {
        log.info("Retrieving all users.");
        return userRepository.findAll();
    }

    @Transactional
    public void addRoleToUser(String username, String roleName) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException(String.format("User with username %s not found", username)));
        Role role = roleRepository.findByName(UserRoles.valueOf(roleName)).orElseThrow(() -> new NotFoundException(String.format("Role with name %s not found", roleName)));
        if( user.getRoles().contains(role) ) {
            log.info("User {} already has the role {}.", username, roleName);
            throw new DuplicateUserRoleException(String.format("User %s already has the role %s.", username, roleName));
        }
        log.info("Adding role {} to user {}.", roleName, username);
        user.getRoles().add(role);
    }
}
