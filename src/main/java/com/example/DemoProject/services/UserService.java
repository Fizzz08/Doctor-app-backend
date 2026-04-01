package com.example.DemoProject.services;

import com.example.DemoProject.Entity.User;
import com.example.DemoProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {  // No need to implement UserDetailsService

    @Autowired
    private UserRepository userRepository;

    // Save user to DB
    public void saveUser(User user) {
        userRepository.save(user);
    }

    // Check if email exists
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // Get user by email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    // Get user by name
    public User getUserByName(String name) {
        return userRepository.findByName(name);
    }
}
