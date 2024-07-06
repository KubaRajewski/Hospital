package com.example.zajecia7doktorki.repository;

import com.example.zajecia7doktorki.domain.Appointment;
import com.example.zajecia7doktorki.domain.Doctor;
import com.example.zajecia7doktorki.domain.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByPatientAndDoctorAndDate(Patient patient, Doctor doctor, LocalDate date);

    Page<Appointment> findByDoctor(Doctor doctor, Pageable pageable);

    Page<Appointment> findByPatient(Patient patient, Pageable pageable);

    @Query("SELECT a.patient FROM Appointment a WHERE a.doctor = :doctor")
    Page<Patient> findPatientsByDoctor(@Param("doctor") Doctor doctor, Pageable pageable);

}
