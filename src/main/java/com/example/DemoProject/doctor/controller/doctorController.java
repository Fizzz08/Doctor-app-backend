package com.example.DemoProject.doctor.controller;

import com.example.DemoProject.doctor.dto.DoctorSlotResponse;
import com.example.DemoProject.exeption.ResourceNotFoundException;
import com.example.DemoProject.Entity.Doctor;
import com.example.DemoProject.doctor.repository.DoctorRepository;
import com.example.DemoProject.user.repository.UserRepository;
import com.example.DemoProject.doctor.services.DoctorService;
import com.example.DemoProject.appointment.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/doctor")
public class doctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    public void DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/search")
    public ResponseEntity<List<Doctor>> getAvailableDoctors(
            @RequestParam String location,
            @RequestParam String specialization,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();  // 401 if no auth
        }

        try {
            // Fetch doctors based on location and specialization
            List<Doctor> doctors = doctorService.getAvailableDoctors(location, specialization);

            if (doctors.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(doctors); // Return 404 if no doctors
            }

            // Process available time slots for each doctor
            doctors.forEach(doctor -> {
                String timeSlots = doctor.getAvailableTime(); // Generate time slots
                doctor.setAvailableTime(String.join(", ", timeSlots)); // Combine as a string
            });

            return ResponseEntity.ok(doctors);  // 200 with data

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);  // 500 if error
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Doctor>> getAllDoctors(
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();

        System.out.println("Request made by user: " + email);

        List<Doctor> getAll = doctorService.getAllDoctors();

        if (getAll != null && !getAll.isEmpty()) {
            return new ResponseEntity<>(getAll, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


     //Save/Add the Doctor
     @PostMapping("/add")
     @PreAuthorize("hasRole('ADMIN')")
     public ResponseEntity<Doctor> createDoctor(
             @RequestBody Doctor doctor,
             @AuthenticationPrincipal UserDetails userDetails) {

         System.out.println("Logged in user: " + userDetails.getUsername());
         System.out.println("Received Doctor: " + doctor);

         try {
             Doctor savedDoctor = doctorService.saveDoctor(doctor);
             return new ResponseEntity<>(savedDoctor, HttpStatus.CREATED);
         } catch (Exception e) {
             e.printStackTrace();
             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
         }
     }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteById(@PathVariable("id") long id) {
        boolean isDeleted = doctorService.deleteDoctorById(id);

        if (isDeleted) {
            return ResponseEntity.ok("Doctor with ID " + id + " deleted successfully.");
        } else {
            return ResponseEntity.status(404).body("Doctor with ID " + id + " not found.");
        }
    }

    //update doctor with id
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Doctor> updateDoctor(
            @PathVariable("id") long id,
            @RequestBody Doctor updatedDoctor,
            @AuthenticationPrincipal UserDetails userDetails) {

        System.out.println("Logged in user: " + userDetails.getUsername());

        // Optional: log user details or audit here if needed

        // Perform update
        Doctor doctor = doctorService.updateDoctor(id, updatedDoctor);
        if (doctor == null) {
            throw new ResourceNotFoundException("Doctor not found with id " + id);
        }

        return new ResponseEntity<>(doctor, HttpStatus.OK);
    }


    @PostMapping("/{id}/uploadImage")
    public ResponseEntity<String> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            String uploadDir = "image/doctors"; // Define your upload directory
            String fileName = file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);

            // Save file locally
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());

            // Update doctor's image path in database
            Doctor doctor = doctorService.findDoctorById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor does not exist with id " + id));
            doctor.setImage(filePath.toString());
            doctorService.saveDoctor(doctor);

            return ResponseEntity.ok("Image uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
        }
    }

    //Get Doctor by Id
    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id) {
        Doctor doctor = doctorService.findDoctorById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor does not exist with id " + id));
        return new ResponseEntity<>(doctor, HttpStatus.OK);
    }


    @GetMapping("/availableSlot")
    public ResponseEntity<DoctorSlotResponse> getAvailableSlots(
            @RequestParam Long doctorId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        System.out.println("Authenticated User: " + username);

        DoctorSlotResponse response = doctorService.getAvailableSlot(doctorId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/bookedSlots")
    public ResponseEntity<List<String>> getBookedSlots(
            @RequestParam Long doctorId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        List<String> bookedSlots =
                appointmentService.getBookedSlots(doctorId, date);

        return ResponseEntity.ok(bookedSlots);
    }


    //not working
    @PostMapping("/saveWithImage")
    public ResponseEntity<String> saveDoctorWithImage(
            @RequestPart("doctor") Doctor doctor,
            @RequestPart("file") MultipartFile file) {
        try {
            String imagePath = doctorService.saveDoctorWithImage(doctor, file);
            return ResponseEntity.ok("Doctor and image saved successfully at: " + imagePath);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving doctor and image.");
        }
    }


    @GetMapping("/details")
    public ResponseEntity<Map<String, Long>> getDoctorDetails(@RequestParam String doctorName) {
        Doctor doctor = doctorRepository.findByName(doctorName)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Map<String, Long> response = new HashMap<>();
        response.put("doctorId", doctor.getId());
        return ResponseEntity.ok(response);
    }


}