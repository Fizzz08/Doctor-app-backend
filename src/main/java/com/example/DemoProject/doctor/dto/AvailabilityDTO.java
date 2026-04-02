package com.example.DemoProject.doctor.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AvailabilityDTO {

    @NotBlank(message = "Available days is required")
    private String availableDays;
    // Example: "Monday, Tuesday, Friday"

    @NotBlank(message = "Available time is required")
    @Pattern(
            regexp = "(?i)(1[0-2]|0?[1-9]):[0-5][0-9] (AM|PM) - (1[0-2]|0?[1-9]):[0-5][0-9] (AM|PM)",
            message = "Time must be in format: h:mm AM/PM - h:mm AM/PM"
    )
    private String availableTime;
    // Example: "09:00 AM - 05:00 PM"

    @Min(value = 5, message = "Slot duration must be at least 5 minutes")
    private int slotDuration;
    // Example: 30 or 60 (minutes)
}
