package com.example.zajecia7doktorki.controller;

import com.example.zajecia7doktorki.command.AppointmentCommand;
import com.example.zajecia7doktorki.command.PatientUpdateCommand;
import com.example.zajecia7doktorki.dto.AppointmentDTO;
import com.example.zajecia7doktorki.dto.PatientDTO;
import com.example.zajecia7doktorki.mapping.AppointmentMapper;
import com.example.zajecia7doktorki.mapping.PatientMapper;
import com.example.zajecia7doktorki.service.AppointmentService;
import com.example.zajecia7doktorki.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Tag(name = "Patient controller", description = "Manage patients, and appointments")
@RequestMapping("/api/v1/patients")
public class PatientController {

    private final PatientService patientService;

    private final AppointmentService appointmentService;
    private final PatientMapper patientMapper;
    private final AppointmentMapper appointmentMapper;

    @Operation(summary = "Get patient details", responses = {
            @ApiResponse(description = "Successful retrieval of patient details", responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDTO.class)))})
    @GetMapping("/get")
    public ResponseEntity<PatientDTO> getPatient() {
        return new ResponseEntity<>(patientMapper.patientEntityToDTO(patientService.getPatient()), HttpStatus.OK);
    }

    @Operation(summary = "Get all patients", description = "Retrieve a paginated list of patients")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of patient list",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)))
    @GetMapping("/getAll")
    public ResponseEntity<Page<PatientDTO>> getPatients(@PageableDefault Pageable pageable) {
        return new ResponseEntity<>(patientService.getAllPatients(pageable)
                .map(patientMapper::patientEntityToDTO), HttpStatus.OK);
    }

    @Operation(summary = "Update patient details")
    @ApiResponse(responseCode = "200", description = "Successful update of patient details",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PatientDTO.class)))
    @PutMapping
    public ResponseEntity<PatientDTO> updatePatient(@RequestBody PatientUpdateCommand patientUpdateCommand) {
        return new ResponseEntity<>(patientMapper.patientEntityToDTO
                (patientService.updatePatient(patientUpdateCommand)), HttpStatus.OK);
    }

    @Operation(summary = "Delete a patient")
    @ApiResponse(responseCode = "200", description = "Successful deletion of patient")
    @DeleteMapping
    public HttpStatus deletePatient() {
        patientService.deletePatient();
        return HttpStatus.OK;
    }

    @Operation(summary = "Create an appointment")
    @ApiResponse(responseCode = "201", description = "Appointment successfully created",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AppointmentDTO.class)))
    @PutMapping("/makeAppointment/{doctorId}")
    public ResponseEntity<AppointmentDTO> createAppointment(@PathVariable("doctorId") Long doctorId,
                                                            @RequestBody @Valid AppointmentCommand appointmentCommand) {
        return new ResponseEntity<>(appointmentMapper.appointmentEntityToDTO((appointmentService
                .createAppointment(appointmentMapper.appointmentCommandToAppointmentEntity(appointmentCommand), doctorId))), HttpStatus.CREATED);
    }

    @Operation(summary = "Cancel an appointment")
    @ApiResponse(responseCode = "200", description = "Appointment cancelled successfully")
    @PutMapping("/appointments/cancel/{appointmentId}")
    public HttpStatus cancelAppointment(@PathVariable("appointmentId") Long appointmentId) {
        appointmentService.cancelAppointmentByPatient(appointmentId);
        return HttpStatus.OK;
    }

    @Operation(summary = "Get patient's appointments")
    @ApiResponse(responseCode = "200", description = "A page of appointments",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)))
    @GetMapping("/appointments")
    public ResponseEntity<Page<AppointmentDTO>> getPatientAppointments(@PageableDefault Pageable pageable) {
        return new ResponseEntity<>(patientService.getPatientAppointments(pageable)
                .map(appointmentMapper::appointmentEntityToDTO), HttpStatus.OK);
    }
}
