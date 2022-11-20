package com.trainerslog.backend.service;

import com.trainerslog.backend.lib.entity.Role;
import com.trainerslog.backend.lib.entity.User;
import com.trainerslog.backend.lib.exception.DuplicateUserRoleException;
import com.trainerslog.backend.lib.exception.NotFoundException;
import com.trainerslog.backend.lib.types.UserRoles;
import com.trainerslog.backend.lib.repositories.RoleRepository;
import com.trainerslog.backend.lib.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private User emptyUser;

    private Role emptyRole;

    @BeforeEach
    void setUp() {
        emptyUser = new User();
        emptyRole = new Role();
    }

    @Test
    void testLoadUserByUsernameWithoutRoles() {
        User user = new User();
        user.setUsername("some-username");
        user.setPassword("some-password");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());

        assertEquals(userDetails, userService.loadUserByUsername("some-username"));
        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    void testLoadUserByUsernameWithRoles() {
        User user = new User();
        user.setUsername("some-username");
        user.setPassword("some-password");
        Role userRole = new Role(null, UserRoles.USER);
        Role trainerRole = new Role(null, UserRoles.TRAINER);
        user.getRoles().add(userRole);
        user.getRoles().add(trainerRole);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthoritiesForUser(user));

        assertEquals(userDetails, userService.loadUserByUsername("some-username"));
        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    void testLoadUserByUsernameThrowsNotFoundException() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        String username = "invalid-user";
        String expectedMessage = String.format("User %s not found in the database.", username);

        Exception exception = assertThrows(NotFoundException.class, () -> userService.loadUserByUsername(username));
        verify(userRepository, times(1)).findByUsername(anyString());
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testCreateUser() {
        when(userRepository.save(any())).thenReturn(emptyUser);
        when(passwordEncoder.encode(any())).thenReturn(emptyUser.getPassword());
        assertEquals(emptyUser, userService.createUser(emptyUser));
        verify(userRepository, times(1)).save(any());
        verify(passwordEncoder, times(1)).encode(any());
    }

    @Test
    void testGetUsers() {
        List<User> emptyUserList = Collections.emptyList();
        when(userRepository.findAll()).thenReturn(emptyUserList);
        assertEquals(emptyUserList, userService.getUsers());
        verify(userRepository, times(1)).findAll();
    }

    @ParameterizedTest
    @EnumSource(UserRoles.class)
    void testAddRoleToUserSingleRoles(UserRoles userRole) {
        String username = "some-username";
        String roleName = userRole.toString();
        Role role = new Role();
        role.setName(userRole);
        User user = new User();
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(roleRepository.findByName(any())).thenReturn(Optional.of(role));

        userService.addRoleToUser(username, roleName);

        assertEquals(List.of(role), user.getRoles());
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(roleRepository, times(1)).findByName(any());

    }

    @Test
    void testAddRoleToUserMultipleDifferentRoles() {
        String username = "some-username";
        String roleName1 = "USER";
        String roleName2 = "ADMIN";
        Role role1 = new Role();
        role1.setName(UserRoles.USER);
        Role role2 = new Role();
        role2.setName(UserRoles.ADMIN);
        User user = new User();
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(roleRepository.findByName(any())).thenReturn(Optional.of(role1)).thenReturn(Optional.of(role2));

        userService.addRoleToUser(username, roleName1);

        assertEquals(List.of(role1), user.getRoles());
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(roleRepository, times(1)).findByName(any());

        userService.addRoleToUser(username, roleName2);

        assertEquals(List.of(role1, role2), user.getRoles());
        verify(userRepository, times(2)).findByUsername(anyString());
        verify(roleRepository, times(2)).findByName(any());
    }

    @Test
    void testAddRoleToUserThrowsNotFoundForUser() {
        String username = "some-username";
        String roleName = "USER";
        String expectedMessage = String.format("User with username %s not found", username);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> userService.addRoleToUser(username, roleName));
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(roleRepository, times(0)).findByName(any());
        assertEquals(expectedMessage, exception.getMessage());

    }

    @Test
    void testAddRoleToUserThrowsNotFoundForRole() {
        String username = "some-username";
        String roleName = "USER";
        String expectedMessage = String.format("Role with name %s not found", roleName);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(emptyUser));
        when(roleRepository.findByName(any())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> userService.addRoleToUser(username, roleName));
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(roleRepository, times(1)).findByName(any());
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testAddRoleToUserThrowsIllegalArgumentException () {
        String username = "some-username";
        String roleName = "invalid-role";
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(emptyUser));
        when(roleRepository.findByName(any())).thenReturn(Optional.of(emptyRole));

        assertThrows(IllegalArgumentException.class, () -> userService.addRoleToUser(username, roleName));
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(roleRepository, times(0)).findByName(any());
    }

    @Test
    void testAddRoleToUserThrowsNullPointerException () {
        String username = "some-username";
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(emptyUser));
        when(roleRepository.findByName(any())).thenReturn(Optional.of(emptyRole));

        assertThrows(NullPointerException.class, () -> userService.addRoleToUser(username, null));
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(roleRepository, times(0)).findByName(any());
    }

    @Test
    void testAddRoleToUserThrowsDuplicateUserRoleException() {
        String username = "some-username";
        String roleName = "ADMIN";
        String expectedMessage = String.format("User %s already has the role %s.", username, roleName);

        Role role = new Role();
        role.setName(UserRoles.ADMIN);

        User user = new User();
        user.setUsername(username);
        user.getRoles().add(role);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(roleRepository.findByName(any())).thenReturn(Optional.of(role));

        Exception exception = assertThrows(DuplicateUserRoleException.class, () -> userService.addRoleToUser(username, roleName));
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(roleRepository, times(1)).findByName(any());
        assertEquals(expectedMessage, exception.getMessage());
    }

    private Collection<SimpleGrantedAuthority> getAuthoritiesForUser(User user) {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().toString()))
                .collect(Collectors.toList());
    }
}
