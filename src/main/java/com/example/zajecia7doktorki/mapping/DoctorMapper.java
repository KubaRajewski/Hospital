package com.example.zajecia7doktorki.mapping;

import com.example.zajecia7doktorki.command.DoctorCommand;
import com.example.zajecia7doktorki.command.DoctorUpdateCommand;
import com.example.zajecia7doktorki.domain.Doctor;
import com.example.zajecia7doktorki.dto.DoctorAppointmentDTO;
import com.example.zajecia7doktorki.dto.DoctorDTO;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface DoctorMapper {

    DoctorDTO doctorEntityToDTO(Doctor doctor);

    DoctorAppointmentDTO doctorEnityToDoctorAppointmentDTO(Doctor doctor);

    Doctor doctorCommandToDoctorEntity(DoctorCommand doctorCommand);

    Doctor doctorUpdateCommandToDoctorEntity(DoctorUpdateCommand doctorUpdateCommand);

}
