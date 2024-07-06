package com.example.zajecia7doktorki.dto;

import com.example.zajecia7doktorki.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {

    private String name;

    private String surname;

    private int age;

    private String pesel;

    private Role role;

    private String login;

}
