package com.fooddelivery.controller;

import com.fooddelivery.dto.UserRequest;
import com.fooddelivery.dto.UserResponse;
import com.fooddelivery.dto.UserUpdateRequest;
import com.fooddelivery.model.Role;
import com.fooddelivery.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest userRequest) {
        UserResponse userResponse = userService.registerUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestParam(required = false) Role role) {

        if (role != null) {
            // Если передан параметр role, фильтруем по роли
            List<UserResponse> users = userService.getUsersByRole(role);
            return ResponseEntity.ok(users);
        } else {
            // Иначе возвращаем всех пользователей
            List<UserResponse> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest updateRequest) {
        UserResponse userResponse = userService.updateUser(id, updateRequest);
        return ResponseEntity.ok(userResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id) {
        UserResponse userResponse = userService.deactivateUser(id);
        return ResponseEntity.ok(userResponse);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserResponse> activateUser(@PathVariable Long id) {
        UserResponse userResponse = userService.activateUser(id);
        return ResponseEntity.ok(userResponse);
    }
}