package com.example.DemoProject.services;

import com.example.DemoProject.Entity.User;
import com.example.DemoProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public void saveUser(User user) {
        user.setPassword(hashPassword(user.getPassword())); // Encrypting before saving
        userRepository.save(user);
    }
}
