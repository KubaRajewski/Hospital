package com.example.zajecia7doktorki.service;

import com.example.zajecia7doktorki.command.PatientUpdateCommand;
import com.example.zajecia7doktorki.domain.Appointment;
import com.example.zajecia7doktorki.domain.Patient;
import com.example.zajecia7doktorki.exception.LoginNotFoundException;
import com.example.zajecia7doktorki.exception.PatientNotFoundException;
import com.example.zajecia7doktorki.repository.AppointmentRepository;
import com.example.zajecia7doktorki.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.zajecia7doktorki.constants.ConstantsUtil.*;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final CustomerRepository customerRepository;

    private final AppointmentRepository appointmentRepository;

    private final CustomerUserDetailsService userDetailsService;

    public Patient getPatient() {
        return getPatient(getUsername());
    }

    public Page<Patient> getAllPatients(Pageable pageable) {
        return customerRepository.findAllByUserType(PATIENT, pageable)
                .map(Patient.class::cast);
    }

    public Patient updatePatient(PatientUpdateCommand patientUpdateCommand) {
        String username = getUsername();
        Patient patientToUpdate = getPatient(username);

        Optional.ofNullable(patientUpdateCommand.getName())
                .ifPresent(patientToUpdate::setName);
        Optional.ofNullable(patientUpdateCommand.getSurname())
                .ifPresent(patientToUpdate::setSurname);
        Optional.of(patientUpdateCommand.getAge()).filter(age -> age > 0)
                .ifPresent(patientToUpdate::setAge);

        return customerRepository.save(patientToUpdate);
    }

    public void deletePatient() {
        String username = getUsername();
        Patient patientToDelete = getPatient(username);

        customerRepository.delete(patientToDelete);
    }

    public Page<Appointment> getPatientAppointments(Pageable pageable) {
        String username = getUsername();
        Patient patient = getPatient(username);

        return appointmentRepository.findByPatient(patient, pageable);
    }

    private String getUsername() {
        return Optional.ofNullable(userDetailsService.getUserDetails())
                .map(UserDetails::getUsername)
                .orElseThrow(() -> new LoginNotFoundException(LOGIN_DOES_NOT_EXIST));
    }

    private Patient getPatient(String username) {
        return customerRepository.findByLogin(username)
                .filter(Patient.class::isInstance)
                .map(Patient.class::cast)
                .orElseThrow(() -> new PatientNotFoundException(PATIENT_WITH_THIS_LOGIN_DOES_NOT_EXIST));
    }
}
