package com.example.DemoProject.appointment.controller;

import com.example.DemoProject.appointment.DTO.AppointmentDTO;
import com.example.DemoProject.Entity.Appointment;
import com.example.DemoProject.appointment.services.AppointmentService;
import com.example.DemoProject.appointment.services.AppointmentServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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

