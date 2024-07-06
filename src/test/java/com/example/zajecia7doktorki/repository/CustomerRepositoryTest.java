package com.example.zajecia7doktorki.repository;

import com.example.zajecia7doktorki.domain.Admin;
import com.example.zajecia7doktorki.domain.Customer;
import com.example.zajecia7doktorki.domain.Doctor;
import com.example.zajecia7doktorki.domain.Patient;
import com.example.zajecia7doktorki.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class CustomerRepositoryTest {
    @Autowired
    private CustomerRepository customerRepository;

    private Patient patient;
    private Doctor doctor;
    private Admin admin;
    @BeforeEach
    void setUp() {
        setUpPatient();
        setUpDoctor();
        setUpAdmin();
    }
    @Test
    void findByIdAndUserType() {
        customerRepository.save(doctor);

        Optional<Customer> doctorById = customerRepository.findByIdAndUserType(doctor.getId(), "DOCTOR");

        assertThat(doctorById).isNotEmpty();
        assertThat(doctorById.get().getName()).isEqualTo(doctor.getName());
        assertThat(doctorById.get().getSurname()).isEqualTo(doctor.getSurname());
        assertThat(doctorById.get().getPesel()).isEqualTo(doctor.getPesel());
    }
    @Test
    void shouldNotFindNonExistingPesel() {
        Optional<Customer> foundPatient = customerRepository.findByPesel("nonExistingPesel");

        assertThat(foundPatient).isEmpty();
    }

//    @Test
//    void findAllByUserType() {
//        customerRepository.save(doctor);
//        customerRepository.save(admin);
//        customerRepository.save(patient);
//
////        List<Customer> customers = customerRepository.findAllByUserType("DOCTOR");
//
//        assertThat(customers).isNotEmpty();
//        assertThat(customers).contains(doctor);
//        assertThat(customers.get(0).getName()).isEqualTo(doctor.getName());
////        assertThat(customers.get().get(0).getSurname()).isEqualTo(doctor.getSurname());
////        assertThat(customers.get().get(0).getPesel()).isEqualTo(doctor.getPesel());
//
//    }

    @Test
    void findByLogin() {
        customerRepository.save(admin);
        // when
        Optional<Customer> adminByLogin = customerRepository.findByLogin(admin.getLogin());

        // then
        assertThat(adminByLogin).isNotEmpty();
        assertThat(adminByLogin.get().getName()).isEqualTo(admin.getName());
        assertThat(adminByLogin.get().getSurname()).isEqualTo(admin.getSurname());
        assertThat(adminByLogin.get().getPesel()).isEqualTo(admin.getPesel());
        //sprawdzac id
    }

    @Test
    void findByPesel() {
        customerRepository.save(patient);
        // when
        Optional<Customer> patientByPesel = customerRepository.findByPesel(patient.getPesel());

        // then
        assertThat(patientByPesel).isNotEmpty();
        assertThat(patientByPesel.get().getName()).isEqualTo(patient.getName());
        assertThat(patientByPesel.get().getSurname()).isEqualTo(patient.getSurname());
        assertThat(patientByPesel.get().getPesel()).isEqualTo(patient.getPesel());
        //sprawdzac id
    }
    private void setUpDoctor() {
        doctor = new Doctor();
        doctor.setName("Janek");
        doctor.setSurname("Kowalski");
        doctor.setSpecialization("Kardiolog");
        doctor.setAge(30);
        doctor.setPesel("22222222222");
        doctor.setLogin("Janek");
        doctor.setPassword("Janek");
        doctor.setRole(Role.USER);
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
    private void setUpAdmin() {
        admin = new Admin();
        admin.setName("Jasiu");
        admin.setSurname("Kowalski");
        admin.setAge(30);
        admin.setPesel("11111111111");
        admin.setLogin("jasiu");
        admin.setPassword("jasiu");
        admin.setRole(Role.USER);
    }
}