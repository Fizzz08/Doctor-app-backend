package com.example.DemoProject.appointment.services;

import com.example.DemoProject.appointment.DTO.AppointmentDTO;
import com.example.DemoProject.doctor.dto.DoctorDTO;
import com.example.DemoProject.user.DTO.UserAppointmentDTO;
import com.example.DemoProject.Entity.Appointment;
import com.example.DemoProject.Entity.Doctor;
import com.example.DemoProject.Entity.User;
import com.example.DemoProject.appointment.repository.AppointmentRepository;
import com.example.DemoProject.doctor.repository.DoctorRepository;
import com.example.DemoProject.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

@Service
public class AppointmentServiceImpl implements AppointmentServices {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Override
    public Map<String, Object> bookAppointment(AppointmentDTO appointmentDTO) {

        Map<String, Object> response = new HashMap<>();

        try {
            // 1️⃣ Validate input
            validateAppointmentRequest(appointmentDTO);

            // 2️⃣ Fetch doctor & user
            Doctor doctor = doctorRepository.findById(
                    appointmentDTO.getDoctor().getDoctorId()
            ).orElseThrow(() -> new RuntimeException("Doctor not found"));

            User user = userRepository.findById(
                    appointmentDTO.getUser().getId()
            ).orElseThrow(() -> new RuntimeException("User not found"));

            // 3️⃣ Normalize time format
            DateTimeFormatter timeFormatter =
                    new DateTimeFormatterBuilder()
                            .parseCaseInsensitive()
                            .appendPattern("hh:mm a")
                            .toFormatter(Locale.ENGLISH);

            String requestedTime = appointmentDTO.getTimeOfAppointment().trim();
            String[] requestedSlot = requestedTime.split(" - ");

            LocalTime requestedStartTime =
                    LocalTime.parse(requestedSlot[0].trim(), timeFormatter);

            LocalTime requestedEndTime =
                    LocalTime.parse(requestedSlot[1].trim(), timeFormatter);

            // 4️⃣ Validate doctor's availability window
            List<String> availableSlots =
                    Arrays.stream(doctor.getAvailableTime().split(","))
                            .map(String::trim)
                            .toList();

            boolean isTimeAvailable = availableSlots.stream().anyMatch(slot -> {
                String[] availableSlot = slot.split(" - ");
                LocalTime availableStartTime =
                        LocalTime.parse(availableSlot[0].trim(), timeFormatter);
                LocalTime availableEndTime =
                        LocalTime.parse(availableSlot[1].trim(), timeFormatter);

                return !requestedStartTime.isBefore(availableStartTime)
                        && !requestedEndTime.isAfter(availableEndTime);
            });

            if (!isTimeAvailable) {
                throw new RuntimeException(
                        "Selected time is not available in the doctor's schedule."
                );
            }

            // 5️⃣ Create appointment
            Appointment appointment =
                    createAppointmentFromDTO(appointmentDTO,
                            appointmentDTO.getTimeOfAppointment());

            appointment.setUser(user);
            appointment.setDoctor(doctor); // important

            // 6️⃣ Save (DB handles duplicates)
            appointmentRepository.save(appointment);

            response.put("success", true);
            response.put("message", "Appointment booked successfully.");

        }
        catch (DataIntegrityViolationException ex) {
            // 🔥 Duplicate booking protection
            response.put("success", false);
            response.put("message",
                    "This slot was just booked by another user. Please select another time.");
        }
        catch (RuntimeException ex) {
            // Other validation errors
            response.put("success", false);
            response.put("message", ex.getMessage());
        }

        return response;
    }

    private void validateAppointmentRequest(AppointmentDTO appointmentDTO) {
        if (appointmentDTO.getUser() == null || appointmentDTO.getUser().getId() == null) {
            throw new RuntimeException("User ID cannot be null");
        }
        if (appointmentDTO.getDoctor().getDoctorId() == null) {
            throw new RuntimeException("Doctor ID cannot be null");
        }
        if (appointmentDTO.getTimeOfAppointment() == null || appointmentDTO.getTimeOfAppointment().isEmpty()) {
            throw new RuntimeException("Time of appointment cannot be null or empty");
        }
        if (appointmentDTO.getAppointmentDate() == null) {
            throw new RuntimeException("Appointment date cannot be null");
        }
    }

    private Appointment createAppointmentFromDTO(AppointmentDTO appointmentDTO, String bookedSlot) {
        // Fetch the Doctor and User entities based on their IDs
        Doctor doctor = doctorRepository.findById(appointmentDTO.getDoctor().getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        User user = userRepository.findById(appointmentDTO.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Appointment appointment = new Appointment();
        appointment.setDoctorName(appointmentDTO.getDoctorName());
        appointment.setAppointmentDate(appointmentDTO.getAppointmentDate());
        appointment.setDay_of_appointment(appointmentDTO.getDay_of_appointment());
        appointment.setTimeOfAppointment(bookedSlot);
        appointment.setBookedDateTime(LocalDateTime.now());
        appointment.setDoctor(doctor);
        appointment.setUser(user);      // Set the User object
        return appointment;
    }


    @Override
    @Transactional
    public AppointmentDTO getAppointmentById(Long appointmentId) {
        // Fetch the appointment with all required associations
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Check and handle null associations for `user` and `doctor`
        UserAppointmentDTO userAppointmentDTO = null;
        if (appointment.getUser() != null) {
            userAppointmentDTO = new UserAppointmentDTO(
                    appointment.getUser().getId(),
                    appointment.getUser().getName()
            );
        }

        DoctorDTO doctorDTO = null;
        if (appointment.getDoctor() != null) {
            doctorDTO = new DoctorDTO(
                    appointment.getDoctor().getId(),
                    appointment.getDoctor().getAvailableTime()
            );
        } else {
            throw new IllegalArgumentException("Doctor information is missing for the appointment.");
        }

        // Create and return the AppointmentDTO
        return new AppointmentDTO(
                appointment.getId(),
                appointment.getDoctorName(),
                appointment.getDay_of_appointment(),
                appointment.getTimeOfAppointment(),
                appointment.getPatient_token(),
                appointment.getAppointmentDate(),
                appointment.getBookedDateTime(),
                doctorDTO,
                userAppointmentDTO
        );
    }


}