package com.example.zajecia7doktorki.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientAppointmentDTO {
    private String name;

    private String surname;

    private int age;

    private String pesel;

}
