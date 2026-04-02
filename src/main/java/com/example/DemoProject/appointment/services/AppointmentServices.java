package com.example.DemoProject.appointment.services;

import com.example.DemoProject.appointment.DTO.AppointmentDTO;

import java.util.Map;

public interface AppointmentServices  {
    Map<String, Object> bookAppointment(AppointmentDTO appointmentDTO);
    AppointmentDTO getAppointmentById(Long appointmentId);
}
