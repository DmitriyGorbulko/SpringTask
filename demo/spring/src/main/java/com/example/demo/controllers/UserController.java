package com.example.demo.controllers;

import com.example.demo.dto.*;
import com.example.demo.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // Конструктор для внедрения UserService
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Создание пользователя
    @PostMapping
    public ResponseEntity<UserPostResponse> createUser(@RequestBody UserPostRequest request) {
        UserPostResponse response = userService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Обновление email пользователя по id
    @PutMapping("/{id}/email")
    public ResponseEntity<UserUpdateResponse> updateUserEmail(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request) {
        UserUpdateResponse response = userService.updateUserEmail(id, request);
        return ResponseEntity.ok(response);
    }

    // Получение пользователя по email (через параметр запроса)
    @GetMapping
    public ResponseEntity<UserGetResponse> getUserByEmail(@RequestParam String email) {
        UserGetRequest request = new UserGetRequest();
        request.setEmail(email);
        UserGetResponse response = userService.getUserByEmail(request);
        return ResponseEntity.ok(response);
    }

    // Удаление пользователя по id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        UserDeleteRequest request = new UserDeleteRequest();
        request.setId(id);
        userService.deleteUser(request);
        return ResponseEntity.noContent().build();
    }
}
