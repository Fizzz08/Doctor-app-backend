package com.example.DemoProject.controller;

import com.example.DemoProject.Security.JwtUtil;
import com.example.DemoProject.repository.UserRepository;
import com.example.DemoProject.services.PasswordEncodeService;
import com.example.DemoProject.Entity.User;
import com.example.DemoProject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/api")
public class loginController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncodeService passwordEncodeService;

    @GetMapping("/userName")
    public ResponseEntity<Map<String, Object>> getLoggedInUserDetails(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);

        // Assuming you have a JwtUtil class to extract user details
        String username = jwtUtil.extractUsername(jwt);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("userName", user.getName());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/userdetails")
    public ResponseEntity<Map<String, Object>> getUserDetailsFromToken(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7); // Remove "Bearer " prefix

            Long userId = jwtUtil.extractUserId(jwt);
            String userName = jwtUtil.extractName(jwt);

            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("userName", userName);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

//    @GetMapping("/username")
//    @ResponseBody
//    public String getLoggedInUsername(@RequestParam String email) {
//        Optional<User> userOptional = userRepository.findByEmail(email);
//
//        return userOptional.map(User::getName)
//                .orElse("User not found");
//    }





}
