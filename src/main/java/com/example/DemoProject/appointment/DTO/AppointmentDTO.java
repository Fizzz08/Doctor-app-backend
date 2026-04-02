package com.example.DemoProject.appointment.DTO;

import com.example.DemoProject.user.DTO.UserAppointmentDTO;
import com.example.DemoProject.doctor.dto.DoctorDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class AppointmentDTO {
    private Long id;
    private String doctorName;
    private String day_of_appointment;
    private String timeOfAppointment;
    private Long patientToken;
    private LocalDate appointmentDate;
    private LocalDateTime bookedDateTime;
    private DoctorDTO doctor;
    private UserAppointmentDTO user;


    public AppointmentDTO(Long id, String doctorName, String dayOfAppointment, String timeOfAppointment, Long patientToken, LocalDate appointmentDate, LocalDateTime bookedDateTime, DoctorDTO doctor, UserAppointmentDTO user) {
        this.id = id;
        this.doctorName = doctorName;
        this.day_of_appointment = dayOfAppointment;
        this.timeOfAppointment = timeOfAppointment;
        this.patientToken = patientToken;
        this.appointmentDate = appointmentDate;
        this.bookedDateTime = bookedDateTime;
        this.doctor = doctor;
        this.user = user;
    }

}
