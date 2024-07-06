package com.example.zajecia7doktorki.controller;

import com.example.zajecia7doktorki.command.AdminCommand;
import com.example.zajecia7doktorki.command.DoctorCommand;
import com.example.zajecia7doktorki.command.PatientCommand;
import com.example.zajecia7doktorki.dto.AdminDTO;
import com.example.zajecia7doktorki.dto.DoctorDTO;
import com.example.zajecia7doktorki.dto.PatientDTO;
import com.example.zajecia7doktorki.mapping.AdminMapper;
import com.example.zajecia7doktorki.mapping.DoctorMapper;
import com.example.zajecia7doktorki.mapping.PatientMapper;
import com.example.zajecia7doktorki.service.RegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Tag(name = "Register controller", description = "Register admins, doctor and patients")
@RequestMapping("/api/v1/register")
public class RegisterController {
    private final RegisterService registerService;
    private final DoctorMapper doctorMapper;
    private final PatientMapper patientMapper;
    private final AdminMapper adminMapper;


    @Operation(summary = "Save a patient", description = "The endpoint through which we can register a new patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient saved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDTO.class))})
    })
    @PostMapping("/patient")
    public ResponseEntity<PatientDTO> savePatient(@Valid @RequestBody PatientCommand patientCommand) {
        return new ResponseEntity<>(patientMapper.patientEntityToDTO(registerService
                .createPatient(patientMapper.patientCommandToPatientEntity(patientCommand))), HttpStatus.CREATED);
    }

    @Operation(summary = "Save a doctor", description = "The endpoint through which we can register a new doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Doctor saved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DoctorDTO.class))})
    })
    @PostMapping("/doctor")
    public ResponseEntity<DoctorDTO> saveDoctor(@RequestBody @Valid DoctorCommand doctorCommand) {
        return new ResponseEntity<>(doctorMapper.doctorEntityToDTO(registerService
                .createDoctor(doctorMapper.doctorCommandToDoctorEntity(doctorCommand))), HttpStatus.CREATED);
    }

    @Operation(summary = "Save an admin", description = "The endpoint through which we can register a new admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Admin saved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AdminDTO.class))})
    })
    @PostMapping("/admin")
    public ResponseEntity<AdminDTO> saveAdmin(@RequestBody @Valid AdminCommand adminCommand) {
        return new ResponseEntity<>(adminMapper.adminEntityToDTO(registerService
                .createAdmin(adminMapper.adminCommandToAdminEntity(adminCommand))), HttpStatus.CREATED);
    }
}
