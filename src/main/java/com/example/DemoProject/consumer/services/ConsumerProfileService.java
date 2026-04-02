package com.example.DemoProject.consumer.services;

import com.example.DemoProject.consumer.DTO.ConsumerProfileDTO;
import com.example.DemoProject.Entity.Profile;
import com.example.DemoProject.Entity.User;
import com.example.DemoProject.consumer.repository.UserProfileRepository;
import com.example.DemoProject.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConsumerProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Profile> getUserProfile() {
        List<Profile> userProfile = userProfileRepository.findAll();
        return userProfile;
    }

    public Profile getProfileByEmail(String email) {
        return userProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Profile not found for email: " + email));
    }


    public Profile addOrUpdateProfile(String email, ConsumerProfileDTO consumerProfileDTO) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found for email: " + email);
        }

        User user = userOptional.get();
        Profile profile = user.getProfile();

        if (profile == null) {
            profile = new Profile();
            profile.setUser(user);
        }

        // Update user's name from ProfileDTO firstName
        user.setName(consumerProfileDTO.getFirstName());
        userRepository.save(user); // Save updated user

        // Map fields from ProfileDTO to Profile
        profile.setFirstName(consumerProfileDTO.getFirstName());
        profile.setLastName(consumerProfileDTO.getLastName());
        profile.setGender(consumerProfileDTO.getGender());
        profile.setDateOfBirth(consumerProfileDTO.getDateOfBirth());
        profile.setAddress(consumerProfileDTO.getAddress());
        profile.setPhoneNumber(consumerProfileDTO.getPhoneNumber());
        profile.setAlternateNumber(consumerProfileDTO.getAlternateNumber());
        profile.setProfileUrl(consumerProfileDTO.getProfileUrl());

        return userProfileRepository.save(profile);
    }

}
