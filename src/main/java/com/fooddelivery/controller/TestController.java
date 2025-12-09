package com.fooddelivery.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/")
    public String hello() {
        return "Food Delivery API is running! Endpoints: POST /users, GET /users/{id}, GET /users";
    }

    @GetMapping("/test")
    public String test() {
        return "Test endpoint works!";
    }
}