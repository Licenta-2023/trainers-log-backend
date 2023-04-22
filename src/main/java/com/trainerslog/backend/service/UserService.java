package com.trainerslog.backend.service;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.trainerslog.backend.lib.entity.Role;
import com.trainerslog.backend.lib.entity.Trainer;
import com.trainerslog.backend.lib.entity.User;
import com.trainerslog.backend.lib.exception.ClientException;
import com.trainerslog.backend.lib.exception.DuplicateUserRoleException;
import com.trainerslog.backend.lib.exception.IncorrectPasswordException;
import com.trainerslog.backend.lib.exception.NotFoundException;
import com.trainerslog.backend.lib.repository.TrainerRepository;
import com.trainerslog.backend.lib.types.PatchUserPasswordRequest;
import com.trainerslog.backend.lib.util.SecurityUtils;
import com.trainerslog.backend.lib.types.PatchUserRequest;
import com.trainerslog.backend.lib.types.UserRoles;
import com.trainerslog.backend.lib.repository.RoleRepository;
import com.trainerslog.backend.lib.repository.UserRepository;
import com.trainerslog.backend.lib.util.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final TrainerRepository trainerRepository;

    private final TrainerService trainerService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException(String.format("User %s not found in the database.", username)));
        log.info("User found in the database {}.", username);
        Collection<SimpleGrantedAuthority> authorities = getAuthoritiesForUser(user);
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    @Transactional
    public User createUser(User user) {
        log.info("Creating user {}.", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User newUser = userRepository.save(user);

        this.addRoleToUser(user.getUsername(), UserRoles.USER.toString());

        return newUser;
    }

    public List<User> getUsers() {
        log.info("Retrieving all users.");
        return userRepository.findAll();
    }

    public User getUser(String username, String bearerToken) {
        if (UserUtils.getRoleFromBearerToken(bearerToken).stream().noneMatch(role -> role.equals("ADMIN"))) {
            UserUtils.throwIfRequestUserNotTheSameAsTargetUser(username, bearerToken);
        }
        return userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException(String.format("User %s not found in the database.", username)));
    }

    @Transactional
    public void addRoleToUser(String username, String roleName) {
        User user = getUser(username);
        Role role = roleRepository.findByName(UserRoles.valueOf(roleName)).orElseThrow(() -> new NotFoundException(String.format("Role with name %s not found", roleName)));
        if( user.getRoles().contains(role) ) {
            log.info("User {} already has the role {}.", username, roleName);
            throw new DuplicateUserRoleException(String.format("User %s already has the role %s.", username, roleName));
        }
        log.info("Adding role {} to user {}.", roleName, username);
        user.getRoles().add(role);
        if(role.getName().equals(UserRoles.TRAINER)) {
            createTrainerWithoutPresence(user);
        }
    }

    @Transactional
    public void patchUser(PatchUserRequest patchUserRequest, String username, String bearerToken) {
        if (UserUtils.getRoleFromBearerToken(bearerToken).stream().noneMatch(role -> role.equals("ADMIN"))) {
            UserUtils.throwIfRequestUserNotTheSameAsTargetUser(username, bearerToken);
        }

        User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException(String.format("User %s not found in the database.", username)));

        if (patchUserRequest.firstName() != null) {
            user.setFirstName(patchUserRequest.firstName());
        }

        if (patchUserRequest.lastName() != null) {
            user.setLastName(patchUserRequest.lastName());
        }

        if (patchUserRequest.dob() != null) {
            user.setDob(patchUserRequest.dob());
        }

        if (patchUserRequest.newRoles().size() > 0 && UserUtils.getRoleFromBearerToken(bearerToken).stream().anyMatch(role -> role.equals(UserRoles.ADMIN.toString()))) {
            handleRolesUpdate(user, patchUserRequest.newRoles());
        }

        if (patchUserRequest.patchTrainerBody().isPresent()) {
            this.trainerService.patchTrainer(username, patchUserRequest.patchTrainerBody().get());
        }

        userRepository.save(user);
    }

    @Transactional
    public void patchUserPassword(PatchUserPasswordRequest patchUserPasswordRequest, String username, String bearerToken) {
        if (UserUtils.getRoleFromBearerToken(bearerToken).stream().noneMatch(role -> role.equals("ADMIN"))) {
            UserUtils.throwIfRequestUserNotTheSameAsTargetUser(username, bearerToken);
        }
        User user = this.getUser(username);

        throwIfCurrentPasswordIsWrong(user, patchUserPasswordRequest.currentPassword());

        user.setPassword(this.passwordEncoder.encode(patchUserPasswordRequest.newPassword()));

        userRepository.save(user);
    }

    private void throwIfCurrentPasswordIsWrong(User user, String currentPassword) {
        boolean currentPasswordMatchesActualPassword = this.passwordEncoder.matches(currentPassword, user.getPassword());
        if (!currentPasswordMatchesActualPassword) {
            throw new IncorrectPasswordException("Incorrect password for user " + user.getUsername());
        }
    }

    private void handleRolesUpdate(User user, List<UserRoles> newRoles) {
        boolean userIsTrainerBeforeUpdate = user.getRoles().stream().anyMatch(role -> role.getName().equals(UserRoles.TRAINER));
        boolean userIsTrainerAfterUpdate = newRoles.stream().anyMatch(role -> role.equals(UserRoles.TRAINER));

        log.info("Removing user roles");
        user.getRoles().removeIf(userRole -> newRoles.stream().noneMatch(newRole -> newRole.equals(userRole.getName())));

        log.info("Adding new user roles");
        newRoles.forEach(newRole -> user.getRoles().add(roleRepository.findByName(newRole).orElseThrow(() -> new NotFoundException(String.format("Role with name %s not found", newRole)))));

        if (!userIsTrainerBeforeUpdate && userIsTrainerAfterUpdate) {
            createTrainerWithoutPresence(user);
        }

        if (userIsTrainerBeforeUpdate && !userIsTrainerAfterUpdate) {
            deleteTrainer(user);
        }
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if( authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                DecodedJWT decodedJWT = SecurityUtils.decodeJWT(refreshToken);
                var isRefreshTokenClaim = decodedJWT.getClaim("isRefreshToken");
                if ( isRefreshTokenClaim.isNull() || !isRefreshTokenClaim.asBoolean() ) {
                    throw new ClientException("Authorization token is not a refresh token!");
                }
                String username = decodedJWT.getSubject();
                User user = getUser(username);
                String accessToken = SecurityUtils.buildAccessTokenFromUserEntity(user, request);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", accessToken);
                tokens.put("refresh_token", refreshToken);
                SecurityUtils.writeToResponseBody(response, tokens);

            } catch (Exception e) {
                response.setStatus(FORBIDDEN.value());
                Map<String, String> errorMap = Map.of("error_message", e.getMessage());
                SecurityUtils.writeToResponseBody(response, errorMap);
            }
        } else {
            throw new ClientException("Refresh token is missing or is invalid.");
        }
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException(String.format("User with username %s not found", username)));
    }

    private Collection<SimpleGrantedAuthority> getAuthoritiesForUser(User user) {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().toString()))
                .collect(Collectors.toList());
    }

    private void createTrainerWithoutPresence(User user) {
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainerRepository.save(trainer);
    }

    private void deleteTrainer(User user) {
        Trainer trainer = trainerRepository.findByUsername(user.getUsername()).orElseThrow(() -> new NotFoundException(String.format("No trainer with username %s found.", user.getUsername())));
        trainer.setUser(null);
        trainerRepository.delete(trainer);
    }

    @Transactional
    public void deleteUser(String username) {
        this.getUser(username);
        log.info("Deleting user " + username);
        this.userRepository.removeByUsername(username);
    }
}
