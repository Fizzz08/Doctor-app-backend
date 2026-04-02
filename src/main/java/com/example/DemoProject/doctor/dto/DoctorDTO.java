package com.example.DemoProject.doctor.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorDTO {
    private Long doctorId;
    private String availableTime;

    // Constructor to initialize from Doctor entity
    public DoctorDTO(Long doctorId, String availableTime) {
        this.doctorId = doctorId;
        this.availableTime = availableTime;
    }
}
