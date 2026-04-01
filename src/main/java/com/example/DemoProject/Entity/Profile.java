package com.example.DemoProject.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name="userProfile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    @JsonBackReference
    private User user;

    private String firstName;

    private String lastName;

    private String gender;

    private LocalDate dateOfBirth;

    private String address;

    private String phoneNumber;

    private String alternateNumber;

    private String profileUrl;

}
