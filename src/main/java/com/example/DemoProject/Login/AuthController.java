package com.example.DemoProject.Login;

import com.example.DemoProject.Security.services.AuthenticationService;
import com.example.DemoProject.Login.auth.AuthenticationRequest;
import com.example.DemoProject.Login.auth.AuthenticationResponse;
import com.example.DemoProject.Login.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/verifyToken")
    public ResponseEntity<?> verifyToken(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(403).body("Invalid or expired token");
        }
        return ResponseEntity.ok().body("{\"Email\": \"" + userDetails.getUsername() + "\"}");
    }




//    @PostMapping("/registers")
//    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterDTO userDTO, BindingResult result) {
//        if (result.hasErrors()) {
//            return ResponseEntity.badRequest().body(result.getAllErrors());
//        }
//
//        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
//            return ResponseEntity.badRequest().body("Passwords do not match!");
//        }
//
//        if (userService.emailExists(userDTO.getEmail())) {
//            return ResponseEntity.badRequest().body("Email already exists!");
//        }
//
//        // Map DTO to Entity
//        User user = new User();
//        user.setName(userDTO.getName());
//        user.setEmail(userDTO.getEmail());
//        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
//
//        userService.saveUser(user);
//
//        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
//        System.out.println("Login attempt for: " + loginRequest.getEmail());
//
//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
//            );
//
//            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
//            String token = jwtUtil.generateToken(userDetails.getUsername());
//
//            System.out.println("Login successful for: " + loginRequest.getEmail());
//            return ResponseEntity.ok(Map.of("token", token));
//        } catch (BadCredentialsException e) {
//            System.out.println("Invalid credentials for: " + loginRequest.getEmail());
//            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
//        }
//    }
//


}
