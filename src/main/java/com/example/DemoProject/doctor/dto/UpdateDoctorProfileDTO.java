package com.example.DemoProject.doctor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateDoctorProfileDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String specialization;

    @NotBlank
    private String location;

    private int yearOfExp;

    private int fees;

    private String image;
}
