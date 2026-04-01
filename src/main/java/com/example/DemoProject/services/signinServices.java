package com.example.DemoProject.services;

import com.example.DemoProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class signinServices {

    @Service
    public class UserService {

        @Autowired
        private UserRepository userRepository;

        // Check if the email exists in the database
        public boolean emailExists(String email) {
            return userRepository.existsByEmail(email);  // Custom query method
        }

        // Other service methods for saving users, etc.
    }

}
