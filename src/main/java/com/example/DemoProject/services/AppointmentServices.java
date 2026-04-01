package com.example.DemoProject.services;

import com.example.DemoProject.DTO.AppointmentDTO;

import java.util.Map;

public interface AppointmentServices  {
    Map<String, Object> bookAppointment(AppointmentDTO appointmentDTO);
    AppointmentDTO getAppointmentById(Long appointmentId);
}
