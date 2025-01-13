package com.jstorr.pact_provider.controller;

import com.jstorr.pact_provider.controller.dto.CreateUserDto;
import com.jstorr.pact_provider.controller.dto.User;
import com.jstorr.pact_provider.controller.dto.UsersDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
public class ProviderController {

    @GetMapping("/users")
    public ResponseEntity<UsersDto> getUsers() {
        UsersDto users = new UsersDto();
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserDto req) {
        User createdUser = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .userId(UUID.randomUUID().toString())
                .build();
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
}
