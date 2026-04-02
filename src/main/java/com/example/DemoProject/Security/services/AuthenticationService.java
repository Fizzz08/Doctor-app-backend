package com.example.DemoProject.Security.services;

import com.example.DemoProject.Entity.Role;
import com.example.DemoProject.Entity.User;
import com.example.DemoProject.Security.JwtUtil;
import com.example.DemoProject.Login.auth.AuthenticationRequest;
import com.example.DemoProject.Login.auth.AuthenticationResponse;
import com.example.DemoProject.Login.auth.RegisterRequest;
import com.example.DemoProject.Entity.Doctor;
import com.example.DemoProject.doctor.repository.DoctorRepository;
import com.example.DemoProject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role role = resolveRole(request.getRole());

        //Create User (AUTH ENTITY)
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        userRepository.save(user);

        //AUTO-CREATE DOCTOR PROFILE IF ROLE = DOCTOR (CRITICAL FIX)
        if (role == Role.DOCTOR) {

            Doctor doctor = new Doctor();
            doctor.setUser(user); // FK mapping (user_id)
            doctor.setName(request.getName());

            // Temporary defaults (dashboard will update later)
            doctor.setLocation("");
            doctor.setSpecialization("");
            doctor.setAvailableDays("");
            doctor.setAvailableTime("09:00 AM - 05:00 PM");
            doctor.setYearOfExp(0);
            doctor.setFees(0);
            doctor.setSlotDuration(60);

            // IMPORTANT: set both sides of relation (prevents JPA issues)
            user.setDoctor(doctor);

            doctorRepository.save(doctor);
        }

        var jwtToken = jwtUtil.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .role(user.getRole().name())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var jwtToken = jwtUtil.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .role(user.getRole().name())
                .build();
    }

    private Role resolveRole(String role) {
        if (role == null) {
            return Role.USER;
        }

        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Role.USER;
        }
    }
}
