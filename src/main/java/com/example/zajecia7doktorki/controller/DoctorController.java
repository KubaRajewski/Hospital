package com.example.zajecia7doktorki.controller;

import com.example.zajecia7doktorki.command.DoctorUpdateCommand;
import com.example.zajecia7doktorki.dto.AppointmentDTO;
import com.example.zajecia7doktorki.dto.DoctorDTO;
import com.example.zajecia7doktorki.dto.PatientAppointmentDTO;
import com.example.zajecia7doktorki.mapping.AppointmentMapper;
import com.example.zajecia7doktorki.mapping.DoctorMapper;
import com.example.zajecia7doktorki.mapping.PatientMapper;
import com.example.zajecia7doktorki.service.AppointmentService;
import com.example.zajecia7doktorki.service.DoctorService;
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


@RestController
@RequiredArgsConstructor
@Tag(name = "Doctor controller", description = "Manage doctors, patients, and appointments")
@RequestMapping("/api/v1/doctors")
public class DoctorController {
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final DoctorMapper doctorMapper;
    private final PatientMapper patientMapper;
    private final AppointmentMapper appointmentMapper;

    @Operation(summary = "Get doctor details", responses = {
            @ApiResponse(description = "Successful retrieval of doctor details", responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DoctorDTO.class)))})
    @GetMapping("/get")
    public ResponseEntity<DoctorDTO> getDoctor() {
        return new ResponseEntity<>(doctorMapper.doctorEntityToDTO(doctorService.getDoctor()), HttpStatus.OK);
    }

    @Operation(summary = "Get all doctors", description = "Retrieve a paginated list of doctors")
    @ApiResponse(responseCode = "200", description = "List of all doctors",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)))
    @GetMapping("/getAll")
    public ResponseEntity<Page<DoctorDTO>> getDoctors(@PageableDefault Pageable pageable) {
        return new ResponseEntity<>(doctorService.getAllDoctors(pageable)
                .map(doctorMapper::doctorEntityToDTO), HttpStatus.OK);
    }

    @Operation(summary = "Update doctor details")
    @ApiResponse(responseCode = "200", description = "Doctor updated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DoctorDTO.class)))
    @PutMapping
    public ResponseEntity<DoctorDTO> updateDoctor(@RequestBody DoctorUpdateCommand doctorUpdateCommand) {
        return new ResponseEntity<>(doctorMapper.doctorEntityToDTO(doctorService.updateDoctor(doctorUpdateCommand)),
                HttpStatus.OK);
    }

    @Operation(summary = "Delete doctor")
    @ApiResponse(responseCode = "200", description = "Doctor deleted successfully")
    @DeleteMapping
    public HttpStatus deleteDoctor() {
        doctorService.deleteDoctor();
        return HttpStatus.OK;
    }

    @Operation(summary = "Cancel an appointment")
    @ApiResponse(responseCode = "200", description = "Appointment cancelled successfully")
    @PutMapping("/appointments/cancel/{appointmentId}")
    public HttpStatus cancelAppointment(@PathVariable("appointmentId") Long appointmentId) {
        appointmentService.cancelAppointmentByDoctor(appointmentId);
        return HttpStatus.OK;
    }

    @Operation(summary = "Mark appointment as successful")
    @ApiResponse(responseCode = "200", description = "Appointment marked as successful")
    @PutMapping("/appointments/success/{appointmentId}")
    public HttpStatus successAppointment(@PathVariable("appointmentId") Long appointmentId) {
        appointmentService.appointmentSuccess(appointmentId);
        return HttpStatus.OK;
    }

    @Operation(summary = "Change health condition of a patient")
    @ApiResponse(responseCode = "200", description = "Health condition changed successfully")
    @PutMapping("/patients/changeHealthCondition/{patientId}")
    public HttpStatus changeHealthCondition(@PathVariable("patientId") Long patientId,
                                            @RequestBody String healthCondition) {
        doctorService.changeHealthCondition(patientId, healthCondition);
        return HttpStatus.OK;
    }

    @Operation(summary = "Get doctor's patients")
    @ApiResponse(responseCode = "200", description = "List of doctor's patients")
    @GetMapping("/patients")
    public ResponseEntity<Page<PatientAppointmentDTO>> getDoctorPatients(@PageableDefault Pageable pageable) {
        return new ResponseEntity<>(doctorService.getDoctorPatients(pageable)
                .map(patientMapper::patientEntityToPatientAppointmentDTO), HttpStatus.OK);
    }

    @Operation(summary = "Get doctor's appointments")
    @ApiResponse(responseCode = "200", description = "A page of appointments",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)))
    @GetMapping("/appointments")
    public ResponseEntity<Page<AppointmentDTO>> getDoctorAppointments(@PageableDefault Pageable pageable) {
        return new ResponseEntity<>(doctorService.getDoctorAppointments(pageable)
                .map(appointmentMapper::appointmentEntityToDTO), HttpStatus.OK);
    }
}
