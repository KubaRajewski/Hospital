package com.example.zajecia7doktorki.mapping;

import com.example.zajecia7doktorki.command.PatientCommand;
import com.example.zajecia7doktorki.domain.Patient;
import com.example.zajecia7doktorki.dto.PatientAppointmentDTO;
import com.example.zajecia7doktorki.dto.PatientDTO;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientDTO patientEntityToDTO(Patient patient);

    Patient patientCommandToPatientEntity(PatientCommand patientCommand);

    PatientAppointmentDTO patientEntityToPatientAppointmentDTO(Patient patient);

}
