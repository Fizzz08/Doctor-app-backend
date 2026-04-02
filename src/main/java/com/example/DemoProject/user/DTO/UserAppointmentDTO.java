package com.example.DemoProject.user.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAppointmentDTO {
    private Long id;
    private String name;

    public UserAppointmentDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
