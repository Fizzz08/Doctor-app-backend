package com.example.DemoProject.user.services;

import com.example.DemoProject.Entity.User;
import com.example.DemoProject.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {  // No need to implement UserDetailsService

    @Autowired
    private UserRepository userRepository;

    // Check if email exists
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // Get user by email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

}
