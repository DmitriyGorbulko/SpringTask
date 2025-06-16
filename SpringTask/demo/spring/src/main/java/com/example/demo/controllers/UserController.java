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

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserPostResponse> createUser(@RequestBody UserPostRequest request) {
        UserPostResponse response = userService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/email")
    public ResponseEntity<UserUpdateResponse> updateUserEmail(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request) {
        UserUpdateResponse response = userService.updateUserEmail(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<UserGetResponse> getUserByEmail(@RequestParam String email) {
        UserGetRequest request = new UserGetRequest();
        request.setEmail(email);
        UserGetResponse response = userService.getUserByEmail(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        UserDeleteRequest request = new UserDeleteRequest();
        request.setId(id);
        userService.deleteUser(request.getId());
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();

        if (message != null && message.contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
        if (message != null && message.contains("already exists")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
    }
}
