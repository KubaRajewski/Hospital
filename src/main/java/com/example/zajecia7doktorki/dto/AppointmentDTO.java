package com.example.zajecia7doktorki.dto;

import com.example.zajecia7doktorki.model.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentDTO {
    private LocalDate date;

    private PatientAppointmentDTO patient;

    private DoctorAppointmentDTO doctor;

    private Status status;

}
