package com.fooddelivery.integration;

import com.fooddelivery.dto.UserRequest;
import com.fooddelivery.model.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void testUserCRUDAndFilterOperations() throws Exception {
                // Создаем пользователей с разными ролями
                UserRequest customerRequest = new UserRequest();
                customerRequest.setUsername("customer1");
                customerRequest.setEmail("customer1@test.com");
                customerRequest.setPassword("password123");
                customerRequest.setRole(Role.CUSTOMER);

                UserRequest adminRequest = new UserRequest();
                adminRequest.setUsername("admin1");
                adminRequest.setEmail("admin1@test.com");
                adminRequest.setPassword("password123");
                adminRequest.setRole(Role.ADMIN);

                // Создаем customer
                MvcResult customerResult = mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                                .andReturn();

                String customerResponse = customerResult.getResponse().getContentAsString();
                Long customerId = objectMapper.readTree(customerResponse).get("id").asLong();

                // Создаем admin
                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(adminRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.role").value("ADMIN"));

                // Тестируем фильтрацию по роли
                mockMvc.perform(get("/users")
                                .param("role", "CUSTOMER"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].role").value("CUSTOMER"));

                mockMvc.perform(get("/users")
                                .param("role", "ADMIN"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].role").value("ADMIN"));

                // Получаем всех пользователей
                mockMvc.perform(get("/users"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(2));

                // Деактивируем пользователя
                mockMvc.perform(delete("/users/{id}", customerId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.active").value(false));

                // Активируем обратно
                mockMvc.perform(patch("/users/{id}/activate", customerId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.active").value(true));

                // Получаем конкретного пользователя
                mockMvc.perform(get("/users/{id}", customerId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(customerId))
                                .andExpect(jsonPath("$.active").value(true));

                // Обновляем пользователя
                mockMvc.perform(put("/users/{id}", customerId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"updatedcustomer\",\"email\":\"updated@test.com\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("updatedcustomer"));
        }

        @Test
        void testUserValidation() throws Exception {
                // Тест валидации при создании пользователя
                UserRequest invalidRequest = new UserRequest();
                invalidRequest.setUsername("ab"); // Слишком короткое имя
                invalidRequest.setEmail("invalid-email"); // Невалидный email
                invalidRequest.setPassword("123"); // Слишком короткий пароль

                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void testDuplicateUser() throws Exception {
                // Создаем первого пользователя
                UserRequest request = new UserRequest();
                request.setUsername("uniqueuser");
                request.setEmail("unique@test.com");
                request.setPassword("password123");

                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated());

                // Пытаемся создать пользователя с тем же username
                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isConflict());
        }
}