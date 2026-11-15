package com.busora.controller;

import com.busora.dto.AuthRequest;
import com.busora.dto.AuthResponse;
import com.busora.dto.RegisterRequest;
import com.busora.model.User;
import com.busora.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        AuthResponse response = userService.register(req);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
        AuthResponse response = userService.authenticate(req);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserProfile(@PathVariable Long id) {
        User user = userService.getUserById(id);
        user.setPassword("PROTECTED"); // Invariant: do not expose password hash/text
        return ResponseEntity.ok(user);
    }
}
