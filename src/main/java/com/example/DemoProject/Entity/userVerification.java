package com.example.DemoProject.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "userVerification")
public class userVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // Store BCrypt hashed password here

    @Column(nullable = false)
    private String fullName;

    private String otpCode;

    private LocalDateTime expiresAt;

    private int resendCount = 0;

    private LocalDateTime lastResendAt;

}
