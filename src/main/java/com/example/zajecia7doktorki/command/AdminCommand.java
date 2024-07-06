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
public class AdminCommand {
    @Size(min = 3, max = 20, message = "Your name should contain between 3 to 20 letters")
    private String name;

    @Size(min = 3, max = 20, message = "Your surname should contain between 3 to 20 letters")
    private String surname;

    @Min(value = 1, message = "Age should not be zero")
    private int age;

    @Pattern(regexp = "^[0-9]{11}$", message = "PESEL must be exactly 11 letters long")
    private String pesel;

    @NotBlank(message = "Login should not be blank")
    private String login;

    @NotBlank(message = "Password should not be blank")
    private String password;
}
