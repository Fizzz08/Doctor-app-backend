package com.example.DemoProject.doctor.controller;

import com.example.DemoProject.Entity.Appointment;
import com.example.DemoProject.doctor.dto.DoctorDashboardResponseDTO;
import com.example.DemoProject.Entity.Doctor;
import com.example.DemoProject.doctor.services.DoctorDashboardService;
import com.example.DemoProject.appointment.repository.AppointmentRepository;
import com.example.DemoProject.doctor.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/doctor/dashboard")
@PreAuthorize("hasRole('DOCTOR')")
public class doctorDashboardController {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorDashboardService doctorDashboardService;

    // ===============================
    // 1. GET LOGGED-IN DOCTOR PROFILE
    // ===============================
    @GetMapping("/me")
    public ResponseEntity<Doctor> getLoggedInDoctor(
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();

        Doctor doctor = doctorRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return ResponseEntity.ok(doctor);
    }

    // ===============================
    // 2. GET DASHBOARD DATA (MAIN API)
    // ===============================
    @GetMapping("/data")
    public ResponseEntity<?> getDashboardData(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int requestsPage,
            @RequestParam(defaultValue = "0") int upcomingPage,
            @RequestParam(defaultValue = "3") int requestsSize,
            @RequestParam(defaultValue = "5") int upcomingSize) {

        DoctorDashboardResponseDTO data =
                doctorDashboardService.getDashboardData(
                        userDetails, requestsPage, upcomingPage,requestsSize,upcomingSize);

        return ResponseEntity.ok(data);
    }


    // ===============================
    // 3. UPDATE DOCTOR PROFILE (SELF)
    // ===============================
    @PutMapping("/profile")
    public ResponseEntity<Doctor> updateProfile(
            @RequestBody Doctor updatedDoctor,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();

        Doctor doctor = doctorRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        doctor.setSpecialization(updatedDoctor.getSpecialization());
        doctor.setYearOfExp(updatedDoctor.getYearOfExp());
        doctor.setLocation(updatedDoctor.getLocation());
        doctor.setFees(updatedDoctor.getFees());
        doctor.setName(updatedDoctor.getName());

        Doctor savedDoctor = doctorRepository.save(doctor);

        return ResponseEntity.ok(savedDoctor);
    }

    // ===============================
    // 4. SAVE AVAILABILITY
    // ===============================
    @PutMapping("/availability")
    public ResponseEntity<Doctor> updateAvailability(
            @RequestBody Doctor availabilityData,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();

        Doctor doctor = doctorRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        doctor.setAvailableTime(availabilityData.getAvailableTime());
        doctor.setAvailableDays(availabilityData.getAvailableDays());
        doctor.setSlotDuration(availabilityData.getSlotDuration());

        Doctor saved = doctorRepository.save(doctor);

        return ResponseEntity.ok(saved);
    }

    // ===============================
    // 6. ACCEPT APPOINTMENT
    // ===============================
    @PutMapping("/appointments/{id}/accept")
    public ResponseEntity<String> acceptAppointment(@PathVariable Long id) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus("Confirmed");
        appointmentRepository.save(appointment);

        return ResponseEntity.ok("Appointment Accepted");
    }

    // ===============================
    // 7. REJECT APPOINTMENT
    // ===============================
    @PutMapping("/appointments/{id}/reject")
    public ResponseEntity<String> rejectAppointment(@PathVariable Long id) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus("Rejected");
        appointmentRepository.save(appointment);

        return ResponseEntity.ok("Appointment Rejected");
    }

    @PutMapping("/listing-status")
    public ResponseEntity<?> updateListingStatus(
            @RequestBody Map<String, Boolean> request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();

        Doctor doctor = doctorRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Boolean isListed = request.get("listed");
        doctor.setListed(isListed);

        doctorRepository.save(doctor);

        return ResponseEntity.ok("Listing status updated");
    }


}
