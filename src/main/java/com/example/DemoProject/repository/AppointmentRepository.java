package com.example.DemoProject.repository;


import com.example.DemoProject.Entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Optional<Appointment> findByDoctorNameAndAppointmentDateAndTimeOfAppointment(
            String doctorName, LocalDate appointmentDate, String timeOfAppointment);

    @Query("SELECT a.timeOfAppointment FROM Appointment a WHERE a.doctorName = :doctorName AND a.appointmentDate = :appointmentDate")
    List<String> findBookedSlots(@Param("doctorName") String doctorName, @Param("appointmentDate") LocalDate appointmentDate);

    boolean existsByDoctorIdAndAppointmentDateAndTimeOfAppointment(Long doctorId, LocalDate appointmentDate, String timeOfAppointment);

    List<Appointment> findByUser_EmailOrderByAppointmentDateAsc(String email);
}