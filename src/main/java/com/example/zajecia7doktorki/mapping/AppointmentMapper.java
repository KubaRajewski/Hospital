package com.example.zajecia7doktorki.mapping;

import com.example.zajecia7doktorki.command.AppointmentCommand;
import com.example.zajecia7doktorki.domain.Appointment;
import com.example.zajecia7doktorki.dto.AppointmentDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {DoctorMapper.class, PatientMapper.class})
public interface AppointmentMapper {

    AppointmentDTO appointmentEntityToDTO(Appointment appointment);

    Appointment appointmentCommandToAppointmentEntity(AppointmentCommand appointmentCommand);
}
