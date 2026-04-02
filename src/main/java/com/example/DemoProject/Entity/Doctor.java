package com.example.DemoProject.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "doctor")
@JsonIgnoreProperties({"appointments"})
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @NotBlank(message = "name is required")
    private String name;

    private String location;

    private String specialization;

    private String availableDays;

    @Pattern(regexp = "(?i)(1[0-2]|0?[1-9]):[0-5][0-9] (AM|PM) - (1[0-2]|0?[1-9]):[0-5][0-9] (AM|PM)",
            message = "Time range must include AM/PM and follow the format 'h:mm AM/PM - h:mm AM/PM'")
    private String availableTime;

    @Column(name = "year_of_exp")
    private int yearOfExp;

    private int fees;

    private String image;

    @Column(name = "slot_duration")
    private int slotDuration;

    @Column(nullable = false)
    private boolean listed = false;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.EAGER,orphanRemoval = true)
    @JsonManagedReference
    private List<Appointment> appointments;

}


