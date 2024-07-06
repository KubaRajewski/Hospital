package com.example.zajecia7doktorki.service;

import com.example.zajecia7doktorki.command.DoctorUpdateCommand;
import com.example.zajecia7doktorki.domain.Appointment;
import com.example.zajecia7doktorki.domain.Doctor;
import com.example.zajecia7doktorki.domain.Patient;
import com.example.zajecia7doktorki.exception.DoctorNotFoundException;
import com.example.zajecia7doktorki.exception.LoginNotFoundException;
import com.example.zajecia7doktorki.exception.PatientNotFoundException;
import com.example.zajecia7doktorki.exception.PermissionDeniedException;
import com.example.zajecia7doktorki.model.HealthCondition;
import com.example.zajecia7doktorki.repository.AppointmentRepository;
import com.example.zajecia7doktorki.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.zajecia7doktorki.constants.ConstantsUtil.*;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final CustomerRepository customerRepository;
    private final AppointmentRepository appointmentRepository;

    private final CustomerUserDetailsService userDetailsService;

    public Doctor getDoctor() {
        return getDoctor(getUsername());
    }

    public Page<Doctor> getAllDoctors(Pageable pageable) {
        return customerRepository.findAllByUserType(DOCTOR, pageable)
                .map(Doctor.class::cast);
    }

    public Doctor updateDoctor(DoctorUpdateCommand doctorUpdateCommand) {
        String username = getUsername();
        Doctor doctorToUpdate = getDoctor(username);

        Optional.ofNullable(doctorUpdateCommand.getName())
                .ifPresent(doctorToUpdate::setName);
        Optional.ofNullable(doctorUpdateCommand.getSurname())
                .ifPresent(doctorToUpdate::setSurname);
        Optional.of(doctorUpdateCommand.getAge()).filter(age -> age > 0)
                .ifPresent(doctorToUpdate::setAge);
        Optional.ofNullable(doctorUpdateCommand.getSpecialization())
                .ifPresent(doctorToUpdate::setSpecialization);

        return customerRepository.save(doctorToUpdate);
    }

    public void deleteDoctor() {
        String username = getUsername();
        Doctor doctorToDelete = getDoctor(username);

        customerRepository.delete(doctorToDelete);
    }

    public Set<Patient> getDoctorPatients() {
        String username = getUsername();
        Doctor doctor = getDoctor(username);

        List<Appointment> doctorAppointments = doctor.getAppointments();
        Set<Patient> patients = new HashSet<>();

        doctorAppointments.forEach(appointment -> patients.add(appointment.getPatient()));
        return patients;
    }

    public Page<Patient> getDoctorPatients(Pageable pageable) {
        String username = getUsername();
        Doctor doctor = getDoctor(username);

        return appointmentRepository.findPatientsByDoctor(doctor, pageable);
    }


    public void changeHealthCondition(Long patientId, String healthCondition) {
        Patient patient = (Patient) customerRepository.findByIdAndUserType(patientId, "PATIENT")
                .orElseThrow(() -> new PatientNotFoundException("Patient with this id does not exist"));

        if (getDoctorPatients().contains(patient)) {
            patient.setHealthCondition(HealthCondition.valueOf(healthCondition));
        } else {
            throw new PermissionDeniedException("Doctor can only change health condition of his patients");
        }
        customerRepository.save(patient);
    }

    public Page<Appointment> getDoctorAppointments(Pageable pageable) {
        String username = getUsername();
        Doctor doctor = getDoctor(username);

        return appointmentRepository.findByDoctor(doctor, pageable);
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
}
