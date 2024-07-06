package com.example.zajecia7doktorki.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatientUpdateCommand {
    @Size(min = 3, max = 20, message = "Your name should contain between 3 to 20 letters")
    private String name;

    @Size(min = 3, max = 20, message = "Your surname should contain between 3 to 20 letters")
    private String surname;

    @Min(value = 1, message = "Age should not be zero")
    private int age;
}
