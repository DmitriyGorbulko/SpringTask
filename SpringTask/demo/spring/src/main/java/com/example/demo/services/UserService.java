package com.example.demo.services;

import com.example.demo.config.KafkaProducerService;
import com.example.demo.dto.*;
import com.example.demo.entities.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;

    public UserService(UserRepository userRepository, KafkaProducerService kafkaProducerService) {
        this.userRepository = userRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    public UserPostResponse createUser(UserPostRequest request) {

        User user = UserMapper.toEntity(request);
        User saved = userRepository.save(user);
        kafkaProducerService.sendUserEvent(new UserEvent(saved.getEmail(), "CREATE"));
        return UserMapper.toCreateResponse(saved);
    }

    public UserUpdateResponse updateUserEmail(Long id, UserUpdateRequest request) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found with id: " + id);
        }
        User user = optionalUser.get();
        UserMapper.updateEntityEmail(request, user);
        User updatedUser = userRepository.save(user);
        return UserMapper.toUpdateResponse(updatedUser);
    }

    public UserGetResponse getUserByEmail(UserGetRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found with email: " + request.getEmail());
        }
        return UserMapper.toGetResponse(optionalUser.get());
    }

    public void deleteUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            userRepository.deleteById(id);
            kafkaProducerService.sendUserEvent(new UserEvent(user.getEmail(), "DELETE"));
        });
    }
}
