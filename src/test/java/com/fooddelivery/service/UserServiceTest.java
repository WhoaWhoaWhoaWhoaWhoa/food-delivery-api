package com.fooddelivery.service;

import com.fooddelivery.dto.UserRequest;
import com.fooddelivery.dto.UserResponse;
import com.fooddelivery.exception.UserAlreadyExistsException;
import com.fooddelivery.exception.UserNotFoundException;
import com.fooddelivery.model.Role;
import com.fooddelivery.model.User;
import com.fooddelivery.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User testAdmin;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setRole(Role.CUSTOMER);
        testUser.setActive(true);
        testUser.setCreatedAt(LocalDateTime.now());

        testAdmin = new User();
        testAdmin.setId(2L);
        testAdmin.setUsername("admin");
        testAdmin.setEmail("admin@example.com");
        testAdmin.setPassword("password123");
        testAdmin.setRole(Role.ADMIN);
        testAdmin.setActive(true);
        testAdmin.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, testAdmin));

        List<UserResponse> users = userService.getAllUsers();

        assertEquals(2, users.size());
        assertEquals("testuser", users.get(0).getUsername());
        assertEquals("admin", users.get(1).getUsername());
    }

    @Test
    void testGetUsersByRole() {
        when(userRepository.findByRole(Role.CUSTOMER)).thenReturn(Arrays.asList(testUser));

        List<UserResponse> customers = userService.getUsersByRole(Role.CUSTOMER);

        assertEquals(1, customers.size());
        assertEquals(Role.CUSTOMER, customers.get(0).getRole());
    }

    @Test
    void testDeactivateUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.deactivateUser(1L);

        assertFalse(response.isActive());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testActivateUser() {
        testUser.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.activateUser(1L);

        assertTrue(response.isActive());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testDeactivateUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deactivateUser(999L));
    }

    @Test
    void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponse response = userService.getUserById(1L);

        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
    }

    @Test
    void testRegisterUserSuccess() {
        UserRequest request = new UserRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setRole(Role.CUSTOMER);

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.registerUser(request);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
    }

    @Test
    void testRegisterUserDuplicateUsername() {
        UserRequest request = new UserRequest();
        request.setUsername("existinguser");
        request.setEmail("new@example.com");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(request));
    }

    @Test
    void testRegisterUserDuplicateEmail() {
        UserRequest request = new UserRequest();
        request.setUsername("newuser");
        request.setEmail("existing@example.com");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(request));
    }
}