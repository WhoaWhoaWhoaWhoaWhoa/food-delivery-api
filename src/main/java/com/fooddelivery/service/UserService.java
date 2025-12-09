package com.fooddelivery.service;

import com.fooddelivery.dto.UserRequest;
import com.fooddelivery.dto.UserResponse;
import com.fooddelivery.dto.UserUpdateRequest;
import com.fooddelivery.exception.UserAlreadyExistsException;
import com.fooddelivery.exception.UserNotFoundException;
import com.fooddelivery.model.Role;
import com.fooddelivery.model.User;
import com.fooddelivery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserResponse registerUser(UserRequest userRequest) {
        System.out.println("DEBUG: Starting registration for user: " + userRequest.getUsername());
        System.out.println("DEBUG: Email: " + userRequest.getEmail());
        System.out.println("DEBUG: Role from request: " + userRequest.getRole());

        // Проверка уникальности username и email
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            System.out.println("DEBUG: Username already exists: " + userRequest.getUsername());
            throw new UserAlreadyExistsException("Username", userRequest.getUsername());
        }

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            System.out.println("DEBUG: Email already exists: " + userRequest.getEmail());
            throw new UserAlreadyExistsException("Email", userRequest.getEmail());
        }

        // Создание нового пользователя
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setPhone(userRequest.getPhone());

        if (userRequest.getRole() != null) {
            System.out.println("DEBUG: Setting role: " + userRequest.getRole());
            user.setRole(userRequest.getRole());
        } else {
            System.out.println("DEBUG: Role is null, using default: CUSTOMER");
            user.setRole(com.fooddelivery.model.Role.CUSTOMER);
        }

        System.out.println("DEBUG: User object created: " + user.getUsername());

        try {
            User savedUser = userRepository.save(user);
            System.out.println("DEBUG: User saved with ID: " + savedUser.getId());

            return convertToResponse(savedUser);
        } catch (Exception e) {
            System.out.println("DEBUG: Exception during save: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public UserResponse updateUser(Long id, UserUpdateRequest updateRequest) {
        System.out.println("DEBUG: Updating user with ID: " + id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Обновляем только те поля, которые переданы
        if (updateRequest.getUsername() != null && !updateRequest.getUsername().equals(user.getUsername())) {
            // Проверяем уникальность нового username
            if (userRepository.existsByUsername(updateRequest.getUsername())) {
                throw new UserAlreadyExistsException("Username", updateRequest.getUsername());
            }
            System.out.println("DEBUG: Updating username to: " + updateRequest.getUsername());
            user.setUsername(updateRequest.getUsername());
        }

        if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(user.getEmail())) {
            // Проверяем уникальность нового email
            if (userRepository.existsByEmail(updateRequest.getEmail())) {
                throw new UserAlreadyExistsException("Email", updateRequest.getEmail());
            }
            System.out.println("DEBUG: Updating email to: " + updateRequest.getEmail());
            user.setEmail(updateRequest.getEmail());
        }

        if (updateRequest.getPassword() != null) {
            System.out.println("DEBUG: Updating password");
            user.setPassword(updateRequest.getPassword());
        }

        if (updateRequest.getPhone() != null) {
            System.out.println("DEBUG: Updating phone to: " + updateRequest.getPhone());
            user.setPhone(updateRequest.getPhone());
        }

        if (updateRequest.getRole() != null) {
            System.out.println("DEBUG: Updating role to: " + updateRequest.getRole());
            user.setRole(updateRequest.getRole());
        }

        if (updateRequest.getActive() != null) {
            System.out.println("DEBUG: Updating active to: " + updateRequest.getActive());
            user.setActive(updateRequest.getActive());
        }

        User updatedUser = userRepository.save(user);
        System.out.println("DEBUG: User updated successfully");

        return convertToResponse(updatedUser);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return convertToResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public UserResponse deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setActive(false);
        User deactivatedUser = userRepository.save(user);

        return convertToResponse(deactivatedUser);
    }

    public UserResponse activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setActive(true);
        User activatedUser = userRepository.save(user);

        return convertToResponse(activatedUser);
    }

    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setActive(user.isActive());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}