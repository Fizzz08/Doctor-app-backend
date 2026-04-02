package com.example.DemoProject.doctor.dto;

import com.example.DemoProject.Entity.Doctor;
import com.example.DemoProject.Entity.Appointment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DoctorDashboardResponseDTO {

    private Doctor doctor;
    private List<Appointment> requests;
    private List<Appointment> upcoming;
    private int requestsTotalPages;
    private int upcomingTotalPages;      // Total pages for upcoming
    private int todayCount;

    // Custom constructor used by service layer
    public DoctorDashboardResponseDTO(Doctor doctor,
                                      List<Appointment> requests,
                                      List<Appointment> upcoming,
                                      int requestsTotalPages,
                                      int upcomingTotalPages,
                                      int todayCount) {
        this.doctor = doctor;
        this.requests = requests;
        this.upcoming = upcoming;
        this.requestsTotalPages = requestsTotalPages;
        this.upcomingTotalPages = upcomingTotalPages;
        this.todayCount = todayCount;
    }
}
