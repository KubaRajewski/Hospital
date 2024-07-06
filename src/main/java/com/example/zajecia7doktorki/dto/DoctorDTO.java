package com.example.zajecia7doktorki.dto;

import com.example.zajecia7doktorki.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDTO {
    private String name;

    private String surname;

    private String specialization;

    private int age;

    private String pesel;

    private Role role;

    private String login;

}
