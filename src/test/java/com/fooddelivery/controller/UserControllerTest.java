package com.fooddelivery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.dto.UserRequest;
import com.fooddelivery.dto.UserResponse;
import com.fooddelivery.model.Role;
import com.fooddelivery.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserResponse testUser;
    private UserResponse testAdmin;

    @BeforeEach
    void setUp() {
        testUser = new UserResponse();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.CUSTOMER);
        testUser.setActive(true);
        testUser.setCreatedAt(LocalDateTime.now());

        testAdmin = new UserResponse();
        testAdmin.setId(2L);
        testAdmin.setUsername("admin");
        testAdmin.setEmail("admin@example.com");
        testAdmin.setRole(Role.ADMIN);
        testAdmin.setActive(true);
        testAdmin.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testGetAllUsers() throws Exception {
        List<UserResponse> users = Arrays.asList(testUser, testAdmin);

        Mockito.when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[1].username").value("admin"));
    }

    @Test
    void testGetUsersByRole() throws Exception {
        List<UserResponse> admins = Arrays.asList(testAdmin);

        Mockito.when(userService.getUsersByRole(Role.ADMIN)).thenReturn(admins);

        mockMvc.perform(get("/users")
                .param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].role").value("ADMIN"));
    }

    @Test
    void testRegisterUser() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("newuser");
        userRequest.setEmail("new@example.com");
        userRequest.setPassword("password123");
        userRequest.setPhone("+79161234567");
        userRequest.setRole(Role.CUSTOMER);

        Mockito.when(userService.registerUser(any(UserRequest.class))).thenReturn(testUser);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testUpdateUser() throws Exception {
        // Тест обновления пользователя
        UserResponse updatedUser = new UserResponse();
        updatedUser.setId(1L);
        updatedUser.setUsername("updateduser");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setRole(Role.CUSTOMER);
        updatedUser.setActive(true);

        Mockito.when(userService.updateUser(eq(1L), any())).thenReturn(updatedUser);

        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"updateduser\",\"email\":\"updated@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"));
    }

    @Test
    void testDeactivateUser() throws Exception {
        UserResponse deactivatedUser = new UserResponse();
        deactivatedUser.setId(1L);
        deactivatedUser.setUsername("testuser");
        deactivatedUser.setActive(false);

        Mockito.when(userService.deactivateUser(1L)).thenReturn(deactivatedUser);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void testActivateUser() throws Exception {
        UserResponse activatedUser = new UserResponse();
        activatedUser.setId(1L);
        activatedUser.setUsername("testuser");
        activatedUser.setActive(true);

        Mockito.when(userService.activateUser(1L)).thenReturn(activatedUser);

        mockMvc.perform(patch("/users/1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void testGetUserById() throws Exception {
        Mockito.when(userService.getUserById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void testGetUserByIdNotFound() throws Exception {
        Mockito.when(userService.getUserById(999L))
                .thenThrow(new com.fooddelivery.exception.UserNotFoundException(999L));

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }
}