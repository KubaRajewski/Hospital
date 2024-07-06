package com.example.zajecia7doktorki.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorCommand {

    @Size(min = 3, max = 20, message = "Your name should contain between 3 to 20 letters")
    private String name;

    @Size(min = 3, max = 20, message = "Your surname should contain between 3 to 20 letters")
    private String surname;

    @NotBlank(message = "Specialization should not be blank")
    private String specialization;

    @Min(value = 25, message = "To be a doctor your age must be higher than 25")
    private int age;

    @Pattern(regexp = "^[0-9]{11}$", message = "PESEL must be exactly 11 letters long")
    private String pesel;

    @NotBlank(message = "Login should not be blank")
    private String login;

    @NotBlank(message = "Password should not be blank")
    private String password;

}
