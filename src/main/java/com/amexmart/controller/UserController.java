package com.amexmart.controller;

import com.amexmart.dto.*;
import com.amexmart.model.User;
import com.amexmart.repository.UserRepository;
import com.amexmart.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;



    // ✅ Register User
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegisterDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setRole("CUSTOMER");

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    // ✅ Get current user profile
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getProfile() {
        return ResponseEntity.ok(userService.getUserProfile());
    }

    // ✅ Update profile
    @PutMapping("/me")
    public ResponseEntity<UserProfileDto> updateProfile(@RequestBody UserProfileDto dto) {
        return ResponseEntity.ok(userService.updateUserProfile(dto));
    }

    // ✅ Get all users (ADMIN only later)
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers().stream()
                .map(u -> modelMapper.map(u, UserResponseDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // ✅ Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(modelMapper.map(user, UserResponseDto.class));
    }

    // ✅ Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
