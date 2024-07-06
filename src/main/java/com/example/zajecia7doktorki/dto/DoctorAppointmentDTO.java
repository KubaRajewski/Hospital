package com.example.zajecia7doktorki.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorAppointmentDTO {
    private String name;

    private String surname;

    private String specialization;

}
