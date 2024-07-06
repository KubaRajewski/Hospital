package com.example.zajecia7doktorki.service;

import com.example.zajecia7doktorki.command.AppointmentCommand;
import com.example.zajecia7doktorki.domain.Appointment;
import com.example.zajecia7doktorki.domain.Doctor;
import com.example.zajecia7doktorki.domain.Patient;
import com.example.zajecia7doktorki.exception.*;
import com.example.zajecia7doktorki.model.Status;
import com.example.zajecia7doktorki.repository.AppointmentRepository;
import com.example.zajecia7doktorki.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

import static com.example.zajecia7doktorki.constants.ConstantsUtil.*;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final CustomerUserDetailsService userDetailsService;


    public Appointment createAppointment(Appointment appointment, Long doctorId) {
        String username = getUsername();
        Patient patient = getPatient(username);

        if (!patient.isAccountNonLocked()) {
            throw new PermissionDeniedException("Your account is locked");
        }

        Doctor doctor = (Doctor) customerRepository.findByIdAndUserType(doctorId, "DOCTOR")
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with this id does not exist"));

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        if (appointmentRepository.existsByPatientAndDoctorAndDate(patient, doctor, appointment.getDate())) {
            throw new AppointmentConflictException("Patient is already signed to this appointment");
        }

        return appointmentRepository.save(appointment);
    }

    public void cancelAppointmentByDoctor(Long appointmentId) {
        Appointment appointment = getAppointment(appointmentId);
        String username = getUsername();
        Doctor doctor = getDoctor(username);

        if (!Objects.equals(appointment.getDoctor().getId(), doctor.getId())) {
            throw new PermissionDeniedException("Doctor can only cancel their own appointments");
        }

        appointment.setStatus(Status.CANCELLED);
        appointmentRepository.save(appointment);
    }

    public void cancelAppointmentByPatient(Long appointmentId) {
        Appointment appointment = getAppointment(appointmentId);
        String username = getUsername();
        Patient patient = getPatient(username);

        if (!Objects.equals(appointment.getPatient().getId(), patient.getId())) {
            throw new PermissionDeniedException("Patient can only cancel their own appointments");
        }

        appointment.setStatus(Status.CANCELLED);
        appointmentRepository.save(appointment);
    }

    public void appointmentSuccess(Long appointmentId) {
        Appointment appointment = getAppointment(appointmentId);
        String username = getUsername();
        Doctor doctor = getDoctor(username);

        if (!Objects.equals(appointment.getDoctor().getId(), doctor.getId())) {
            throw new PermissionDeniedException("Doctor can only success their own appointments");
        }
        appointment.setStatus(Status.SUCCESSFUL);
        appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(Long id, AppointmentCommand appointmentCommand) {
        Appointment appointmentToUpdate = getAppointment(id);

        Optional.ofNullable(appointmentCommand.getDate())
                .ifPresent(appointmentToUpdate::setDate);

        return appointmentRepository.save(appointmentToUpdate);
    }

    public void deleteAppointment(Long id) {
        Appointment appointmentToDelete = getAppointment(id);
        appointmentRepository.delete(appointmentToDelete);
    }

    private Appointment getAppointment(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment with id " + appointmentId + " not found"));
    }

    private String getUsername() {
        return Optional.ofNullable(userDetailsService.getUserDetails())
                .map(UserDetails::getUsername)
                .orElseThrow(() -> new LoginNotFoundException(LOGIN_DOES_NOT_EXIST));
    }

    private Doctor getDoctor(String username) {
        return customerRepository.findByLogin(username)
                .filter(Doctor.class::isInstance)
                .map(Doctor.class::cast)
                .orElseThrow(() -> new DoctorNotFoundException(DOCTOR_WITH_THIS_LOGIN_DOES_NOT_EXIST));
    }

    private Patient getPatient(String username) {
        return customerRepository.findByLogin(username)
                .filter(Patient.class::isInstance)
                .map(Patient.class::cast)
                .orElseThrow(() -> new PatientNotFoundException(PATIENT_WITH_THIS_LOGIN_DOES_NOT_EXIST));
    }
}
