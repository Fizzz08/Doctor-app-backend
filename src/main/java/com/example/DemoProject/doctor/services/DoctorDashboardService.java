package com.example.DemoProject.doctor.services;

import com.example.DemoProject.Entity.Appointment;
import com.example.DemoProject.doctor.dto.AvailabilityDTO;
import com.example.DemoProject.doctor.dto.DoctorDashboardResponseDTO;
import com.example.DemoProject.doctor.dto.UpdateDoctorProfileDTO;
import com.example.DemoProject.Entity.Doctor;
import com.example.DemoProject.doctor.repository.DoctorRepository;
import com.example.DemoProject.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorDashboardService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    // ===============================
    // GET LOGGED-IN DOCTOR (JWT EMAIL)
    // ===============================
    public Doctor getLoggedInDoctor(UserDetails userDetails) {

        if (userDetails == null) {
            throw new RuntimeException("Unauthorized: User not authenticated");
        }

        String email = userDetails.getUsername();

        return doctorRepository.findByUser_Email(email)
                .orElseThrow(() ->
                        new RuntimeException("Doctor not found for email: " + email));
    }

    // ===============================
    // GET FULL DASHBOARD DATA
    // ===============================
    @Transactional(readOnly = true)
    public DoctorDashboardResponseDTO getDashboardData(
            UserDetails userDetails,
            int requestsPage,
            int upcomingPage,
            int requestsSize,
            int upcomingSize) {

        Doctor doctor = getLoggedInDoctor(userDetails);
        Long doctorId = doctor.getId();

        Pageable requestsPageable = PageRequest.of(requestsPage, requestsSize);
        Pageable upcomingPageable = PageRequest.of(upcomingPage, upcomingSize);
        LocalDate today = LocalDate.now();

        // Pending Appointment Requests (Paginated)
        Page<Appointment> pendingRequests =
                appointmentRepository.findByDoctor_IdAndStatusAndAppointmentDateGreaterThanEqual(
                        doctorId, "Pending", today, requestsPageable);

        // Upcoming Confirmed Appointments (Paginated + Sorted)
        Page<Appointment> upcomingAppointments =
                appointmentRepository.findByDoctor_IdAndStatusAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAsc(
                        doctorId, "Confirmed", today, upcomingPageable);

        // Today's Appointments Count
        int todayCount =
                appointmentRepository.countByDoctor_IdAndStatusAndAppointmentDate(
                        doctorId, "Confirmed", today);

        return new DoctorDashboardResponseDTO(
                doctor,
                pendingRequests.getContent(),
                upcomingAppointments.getContent(),
                pendingRequests.getTotalPages(),
                upcomingAppointments.getTotalPages(),
                todayCount
        );
    }

    // ===============================
    // UPDATE DOCTOR PROFILE (SELF)
    // ===============================
    public Doctor updateProfile(UserDetails userDetails, UpdateDoctorProfileDTO dto) {

        Doctor doctor = getLoggedInDoctor(userDetails);

        if (dto.getName() != null && !dto.getName().isBlank()) {
            doctor.setName(dto.getName().trim());
        }

        if (dto.getSpecialization() != null && !dto.getSpecialization().isBlank()) {
            doctor.setSpecialization(dto.getSpecialization().trim());
        }

        if (dto.getLocation() != null && !dto.getLocation().isBlank()) {
            doctor.setLocation(dto.getLocation().trim());
        }

        // Match your entity fields exactly
        doctor.setYearOfExp(dto.getYearOfExp());
        doctor.setFees(dto.getFees());

        // Optional image update
        if (dto.getImage() != null && !dto.getImage().isBlank()) {
            doctor.setImage(dto.getImage());
        }

        return doctorRepository.save(doctor);
    }

    // ===============================
    // UPDATE AVAILABILITY (WITH SLOT DURATION)
    // ===============================
    public Doctor updateAvailability(UserDetails userDetails, AvailabilityDTO dto) {

        Doctor doctor = getLoggedInDoctor(userDetails);

        // Available Days (e.g., "Monday, Tuesday")
        if (dto.getAvailableDays() != null && !dto.getAvailableDays().isBlank()) {
            doctor.setAvailableDays(dto.getAvailableDays().trim());
        }

        // Available Time (validated by regex in entity)
        if (dto.getAvailableTime() != null && !dto.getAvailableTime().isBlank()) {
            doctor.setAvailableTime(dto.getAvailableTime().trim());
        }

        // ⭐ IMPORTANT: Slot Duration (30 / 60 mins)
        if (dto.getSlotDuration() > 0) {
            doctor.setSlotDuration(dto.getSlotDuration());
        }

        return doctorRepository.save(doctor);
    }

    // ===============================
    // ACCEPT APPOINTMENT (SECURE)
    // ===============================
    public void acceptAppointment(Long appointmentId, UserDetails userDetails) {

        Doctor doctor = getLoggedInDoctor(userDetails);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // SECURITY CHECK: Doctor can only accept their own appointments
        if (appointment.getDoctor().getId() != doctor.getId()) {
            throw new RuntimeException("Unauthorized: Cannot modify another doctor's appointment");
        }


        appointment.setStatus("Confirmed");
        appointmentRepository.save(appointment);
    }

    // ===============================
    // REJECT APPOINTMENT (SECURE)
    // ===============================
    public void rejectAppointment(Long appointmentId, UserDetails userDetails) {

        Doctor doctor = getLoggedInDoctor(userDetails);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // SECURITY CHECK: Doctor can only modify their own appointments
        if (appointment.getDoctor() == null ||
                appointment.getDoctor().getId() != doctor.getId()) {

            throw new RuntimeException("Unauthorized: Cannot modify another doctor's appointment");
        }


        appointment.setStatus("Rejected");
        appointmentRepository.save(appointment);
    }

}
