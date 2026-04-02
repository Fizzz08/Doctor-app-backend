package com.example.DemoProject.appointment.services;

import com.example.DemoProject.Entity.Appointment;
import com.example.DemoProject.appointment.repository.AppointmentRepository;
import com.example.DemoProject.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    //get Booked slots
    public List<String> getBookedSlots(Long doctorId, LocalDate appointmentDate) {

        List<Appointment> appointments =
                appointmentRepository.findByDoctor_IdAndAppointmentDate(doctorId, appointmentDate);

        return appointments.stream()
                .map(Appointment::getTimeOfAppointment)
                .toList();
    }

    //get the appointment from db
    public List<Appointment> getAllAppointment() {
        return appointmentRepository.findAll();
    }


    // Method to delete appointment by ID
    public Boolean deleteAppointmentById(Long appointmentId) {
        // Retrieve the appointment by ID
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);

        if (optionalAppointment.isEmpty()) {
            return false;
        }
        Appointment appointment = optionalAppointment.get();
        appointment.setUser(null);
        appointmentRepository.save(appointment);
        appointmentRepository.deleteById(appointmentId);
        return true;
    }

    // Fetch recent appointments by user email
    public List<Appointment> getRecentAppointmentsByEmail(String email) {
        return appointmentRepository.findByUser_EmailOrderByAppointmentDateAsc(email);
    }
}
