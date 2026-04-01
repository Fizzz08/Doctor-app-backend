package com.example.DemoProject.controller;

import com.example.DemoProject.DTO.AppointmentDTO;
import com.example.DemoProject.Entity.Appointment;
import com.example.DemoProject.Entity.User;
import com.example.DemoProject.repository.UserRepository;
import com.example.DemoProject.services.AppointmentService;
import com.example.DemoProject.services.AppointmentServices;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/bookAppointment")
public class AppointmentController {

    @Autowired
    private AppointmentServices appointmentServices;

    @Autowired
    private AppointmentService appointmentService;

    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);



    @PostMapping("/book")
    public ResponseEntity<Map<String, Object>> bookAppointment(
            @RequestBody AppointmentDTO appointmentDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "User is not authenticated."));
        }

        logger.info("Booking appointment for user: {}", userDetails.getUsername());
        logger.info("Received appointment data: {}", appointmentDTO);

        Map<String, Object> response = appointmentServices.bookAppointment(appointmentDTO);
        return ResponseEntity.ok(response);
    }



    //getAllAppointments
    @GetMapping("/getAll")
    public ResponseEntity<List<Appointment>> getAppointments(){
        List<Appointment> getAll = appointmentService.getAllAppointment();
        if (getAll!=null && !getAll.isEmpty()){
            return new ResponseEntity<>(getAll,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @GetMapping("/{appointmentId}")
    public ResponseEntity<?> getAppointmentById(@PathVariable Long appointmentId) {
        AppointmentDTO appointmentDTO = appointmentServices.getAppointmentById(appointmentId);
        if (appointmentDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(appointmentDTO);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("message", "Appointment not found"));
    }


    @GetMapping("/bookedSlots")
    public ResponseEntity<List<String>> getBookedSlot(
            @RequestParam String doctorName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        System.out.println("Authenticated User: " + username); // Optional logging

        try {
            List<String> bookedSlots = appointmentService.getBookedSlots(doctorName, date);
            return ResponseEntity.ok(bookedSlots);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteAppointment(@PathVariable Long id) {
        Boolean isDeleted = appointmentService.deleteAppointmentById(id);
        if(isDeleted){
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }

    }


    @GetMapping("/recent-appointments")
    public ResponseEntity<?> getRecentAppointments(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Unauthorized - No valid token"));
        }

        String email = userDetails.getUsername();
        List<Appointment> appointments = appointmentService.getRecentAppointmentsByEmail(email);

        if (appointments.isEmpty()) {
            return ResponseEntity.ok(Collections.singletonMap("message", "No recent appointments found"));
        }

        return ResponseEntity.ok(appointments);
    }


}

