package com.example.DemoProject.repository;

import com.example.DemoProject.Entity.Doctor;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    @Query("SELECT d.availableTime FROM Doctor d WHERE d.name = :doctorName")
    List<String> findSlotsByDoctor(String doctorName);

    List<Doctor> findByLocationAndSpecialization(String location, String specialization);

    Optional<Doctor> findByName(String name);

    @Transactional
    @Modifying
    @Query("DELETE FROM Appointment a WHERE a.doctor.id = :doctorId")
    void deleteAppointmentsByDoctorId(@Param("doctorId") Long doctorId);



}
