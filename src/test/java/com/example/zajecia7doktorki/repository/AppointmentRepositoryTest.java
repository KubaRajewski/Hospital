package com.example.zajecia7doktorki.repository;

import com.example.zajecia7doktorki.domain.Appointment;
import com.example.zajecia7doktorki.domain.Doctor;
import com.example.zajecia7doktorki.domain.Patient;
import com.example.zajecia7doktorki.model.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Patient patient;

    private Doctor doctor;
    private Appointment appointment;

    private LocalDate date;

    @BeforeEach
    void setUp() {
        setUpPatient();
        setUpDoctor();
        date = LocalDate.now();
        setUpAppointment();
        customerRepository.save(patient);
        customerRepository.save(doctor);
    }

    @Test
    void shouldReturnTrueWhenAppointmentExists() {
        // given
       appointmentRepository.save(appointment);

        // when
        boolean exists = appointmentRepository.existsByPatientAndDoctorAndDate(patient, doctor, date);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenAppointmentDoesNotExist() {

        // when
        boolean exists = appointmentRepository.existsByPatientAndDoctorAndDate(patient, doctor, date);

        // then
        assertThat(exists).isFalse();
    }
    private void setUpPatient() {
        patient = new Patient();
        patient.setName("Jan");
        patient.setSurname("Kowalski");
        patient.setAge(30);
        patient.setPesel("12345678901");
        patient.setLogin("jan");
        patient.setPassword("jan");
        patient.setRole(Role.USER);
    }

    private void setUpDoctor() {
        doctor = new Doctor();
        doctor.setName("Jan");
        doctor.setSurname("Kowalski");
        doctor.setSpecialization("Kardiolog");
        doctor.setAge(30);
        doctor.setPesel("12345678931");
        doctor.setLogin("janek");
        doctor.setPassword("jan");
        doctor.setRole(Role.USER);
    }
    private void setUpAppointment() {
        appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setDate(date);
        appointment.setCreatedDate(LocalDate.now());
    }
}