package com.busora.service;

import com.busora.dto.AuthRequest;
import com.busora.dto.AuthResponse;
import com.busora.dto.RegisterRequest;
import com.busora.exception.ResourceNotFoundException;
import com.busora.model.Role;
import com.busora.model.User;
import com.busora.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            return AuthResponse.failure("An account with email " + req.getEmail() + " already exists.");
        }

        Role assignedRole = (req.getRole() != null) ? req.getRole() : Role.USER;
        User user = new User(
            req.getFullName(),
            req.getEmail(),
            req.getPassword(), // Note: plain comparison for academic demonstration
            req.getPhone(),
            assignedRole
        );

        User saved = userRepository.save(user);
        return AuthResponse.success(saved.getId(), saved.getFullName(), saved.getEmail(), saved.getPhone(), saved.getRole());
    }

    public AuthResponse authenticate(AuthRequest req) {
        User user = userRepository.findByEmail(req.getEmail().trim().toLowerCase())
            .orElse(null);

        if (user == null || !user.getPassword().equals(req.getPassword())) {
            return AuthResponse.failure("Invalid email or password. Please try again.");
        }

        return AuthResponse.success(user.getId(), user.getFullName(), user.getEmail(), user.getPhone(), user.getRole());
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }
}
