package com.example.DemoProject.appointment.repository;

import com.example.DemoProject.Entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // ===== EXISTING (DO NOT TOUCH) =====
    Optional<Appointment> findByDoctorNameAndAppointmentDateAndTimeOfAppointment(
            String doctorName, LocalDate appointmentDate, String timeOfAppointment);

    List<Appointment> findByDoctor_IdAndAppointmentDate(Long doctorId, LocalDate appointmentDate);

    boolean existsByDoctorIdAndAppointmentDateAndTimeOfAppointment(
            Long doctorId, LocalDate appointmentDate, String timeOfAppointment);

    List<Appointment> findByUser_EmailOrderByAppointmentDateAsc(String email);


    // =========================================================
    // ===== ADD BELOW METHODS FOR DOCTOR DASHBOARD ONLY =======
    // =========================================================

    // 1️⃣ Pending Requests (future only)
    Page<Appointment> findByDoctor_IdAndStatusAndAppointmentDateGreaterThanEqual(
            Long doctorId,
            String status,
            LocalDate date,
            Pageable pageable
    );

    // 2️⃣ Upcoming Confirmed (future only, sorted ascending)
    Page<Appointment> findByDoctor_IdAndStatusAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAsc(
            Long doctorId,
            String status,
            LocalDate date,
            Pageable pageable
    );

    // 3️⃣ Count Today's Confirmed Appointments
    int countByDoctor_IdAndStatusAndAppointmentDate(
            Long doctorId,
            String status,
            LocalDate appointmentDate
    );


}
