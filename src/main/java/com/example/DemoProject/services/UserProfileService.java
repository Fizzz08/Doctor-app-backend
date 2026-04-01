package com.example.DemoProject.services;

import com.example.DemoProject.DTO.ProfileDTO;
import com.example.DemoProject.Entity.Profile;
import com.example.DemoProject.Entity.User;
import com.example.DemoProject.repository.UserProfileRepository;
import com.example.DemoProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserProfileService {

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



    public Profile addOrUpdateProfile(String email, ProfileDTO profileDTO) {
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
        user.setName(profileDTO.getFirstName());
        userRepository.save(user); // Save updated user

        // Map fields from ProfileDTO to Profile
        profile.setFirstName(profileDTO.getFirstName());
        profile.setLastName(profileDTO.getLastName());
        profile.setGender(profileDTO.getGender());
        profile.setDateOfBirth(profileDTO.getDateOfBirth());
        profile.setAddress(profileDTO.getAddress());
        profile.setPhoneNumber(profileDTO.getPhoneNumber());
        profile.setAlternateNumber(profileDTO.getAlternateNumber());
        profile.setProfileUrl(profileDTO.getProfileUrl());

        return userProfileRepository.save(profile);
    }

}
