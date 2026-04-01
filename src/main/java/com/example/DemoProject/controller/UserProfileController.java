package com.example.DemoProject.controller;

import com.example.DemoProject.DTO.ProfileDTO;
import com.example.DemoProject.Entity.Profile;
import com.example.DemoProject.Entity.User;
import com.example.DemoProject.repository.UserProfileRepository;
import com.example.DemoProject.services.UserProfileService;
import com.example.DemoProject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/v1/userProfile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserService userService;


    @PostMapping("/add")
    public ResponseEntity<?> addOrUpdateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProfileDTO profileDTO) {
        try {
            String email = userDetails.getUsername();  // Assuming email is username in your UserDetails implementation
            Profile savedProfile = userProfileService.addOrUpdateProfile(email, profileDTO);
            return ResponseEntity.ok(savedProfile);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }


    @GetMapping("/getAll")
    public ResponseEntity<List<Profile>> getProfile() {
        List<Profile> getAll = userProfileService.getUserProfile();
        if(getAll!=null && !getAll.isEmpty()){
            return new ResponseEntity<>(getAll, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


//    @GetMapping("/get-profile")
//    public ResponseEntity<Profile> getProfile(@RequestParam String email) {
//        Profile profile = userProfileService.getProfileByEmail(email);
//        return ResponseEntity.ok(profile);
//    }

    @GetMapping("/get-profile")
    public ResponseEntity<Profile> getProfile(@AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();

        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Fetch User entity (assuming you have User table with username/firstName)
        User user = userService.findByEmail(email);
        System.out.println("backend userName(user)"+user);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Profile profile = userProfileService.getProfileByEmail(email);
        if (profile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(profile);
    }

}
