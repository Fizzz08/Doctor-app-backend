package com.example.DemoProject.doctor.services;

import com.example.DemoProject.doctor.dto.DoctorSlotResponse;
import com.example.DemoProject.exeption.ResourceNotFoundException;
import com.example.DemoProject.Entity.Doctor;
import com.example.DemoProject.appointment.repository.AppointmentRepository;
import com.example.DemoProject.doctor.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    AppointmentRepository appointmentRepository;

    // Method to fetch doctors based on location and specialization
    public List<Doctor> getAvailableDoctors(String location, String specialization) {
        List<Doctor> doctors = doctorRepository.findByLocationAndSpecializationAndListedTrue(location, specialization);

        // Generate time slots for each doctor
        doctors.forEach(doctor -> {
            String timeSlots = doctor.getAvailableTime();
            doctor.setAvailableTime(String.join(", ", timeSlots)); // Format time slots as a single string
        });

        return doctors;
    }

    // Method to fetch all doctors
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        // Generate time slots for each doctor (optional if needed)
        doctors.forEach(doctor -> {
            String timeSlots = doctor.getAvailableTime();
            doctor.setAvailableTime(String.join(", ", timeSlots)); // Format time slots as a single string
        });
        return doctors;
    }

    //save doctor
    public Doctor saveDoctor(@RequestBody Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    //delete doctor by id
    public boolean deleteDoctorById(long id) {
        if (!doctorRepository.existsById(id)) {
            return false;
        }
        doctorRepository.deleteAppointmentsByDoctorId(id);
        // Now delete the doctor
        doctorRepository.deleteById(id);
        return true;
    }

    //UPDATE the doctor
    public Doctor updateDoctor(long id, Doctor updatedDoctor) {
        Optional<Doctor> existingDoctorOpt = doctorRepository.findById(id);

        if (existingDoctorOpt.isPresent()) {
            Doctor existingDoctor = existingDoctorOpt.get();

            // Update fields in the existing doctor with the new data
            existingDoctor.setName(updatedDoctor.getName());
            existingDoctor.setSpecialization(updatedDoctor.getSpecialization());
            existingDoctor.setLocation(updatedDoctor.getLocation());
            existingDoctor.setAvailableDays(updatedDoctor.getAvailableDays());
            existingDoctor.setAvailableTime(updatedDoctor.getAvailableTime());
            existingDoctor.setYearOfExp(updatedDoctor.getYearOfExp());
            existingDoctor.setFees(updatedDoctor.getFees());

            return doctorRepository.save(existingDoctor);
        }
        return null;
    }


    public Optional<Doctor> findDoctorById(Long id) {
        return doctorRepository.findById(id);
    }
    @Value("${file.upload-dir}")
    private String uploadDir;
    public Doctor uploadDoctorImage(Long doctorId, MultipartFile file) throws IOException {
        // Ensure the upload directory exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Get the file's original name and save it to the upload directory
        String fileName = doctorId + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());
        // Update the doctor's image path in the database
        Doctor doctor = findDoctorById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor does not exist with id " + doctorId));
        doctor.setImage(fileName); // Save only the relative path
        return saveDoctor(doctor);
    }

    public String getDoctorImagePath(Doctor doctor) {
        return uploadDir + doctor.getImage();
    }



    public DoctorSlotResponse getAvailableSlot(Long doctorId) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        List<String> availableTime = Arrays.stream(doctor.getAvailableTime().split(","))
                .map(String::trim)
                .toList();

        return new DoctorSlotResponse(
                availableTime,
                doctor.getSlotDuration()
        );
    }


    public String saveDoctorWithImage(Doctor doctor, MultipartFile file) throws IOException {
        Doctor savedDoctor = saveDoctor(doctor);

        // Handle file upload
        String uploadDir = "image/doctors";
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        // Create directories if not exist
        Files.createDirectories(filePath.getParent());

        // Save file to disk
        Files.write(filePath, file.getBytes());

        // Update the doctor's image path in the database
        savedDoctor.setImage(filePath.toString());
        saveDoctor(savedDoctor); // Update the doctor with the image path

        return filePath.toString();
    }

}
