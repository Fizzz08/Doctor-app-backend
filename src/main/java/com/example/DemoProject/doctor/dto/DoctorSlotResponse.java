package com.example.DemoProject.doctor.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class DoctorSlotResponse {

    private List<String> availableTime;
    private Integer slotDuration;

    public DoctorSlotResponse(List<String> availableTime, Integer slotDuration) {
        this.availableTime = availableTime;
        this.slotDuration = slotDuration;
    }
}