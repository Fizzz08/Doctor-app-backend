package com.example.DemoProject.Entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "appointment",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_doctor_date_time",
                        columnNames = {
                                "doctor_id",
                                "appointment_date",
                                "time_of_appointment"
                        }
                )
        }
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Ensure ID is auto-generated
    private Long id;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @NotBlank
    private String doctorName;

    private LocalDateTime bookedDateTime;

    @NotNull
    @Column(name = "appointment_date")
    private LocalDate appointmentDate;

    @NotBlank
    private String day_of_appointment;

    @Column(name = "time_of_appointment")
    private String timeOfAppointment;

    private Long patient_token;

    @Column(columnDefinition = "varchar(10) default 'Pending'")
    private String status = "Pending";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id")
    @JsonBackReference
    private Doctor doctor;


    // Method to generate a random 4-digit token
    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = "Pending";
        }

        Random random = new Random();
        int token = random.nextInt(9000) + 1000;
        this.patient_token = (long) token;
    }

}
