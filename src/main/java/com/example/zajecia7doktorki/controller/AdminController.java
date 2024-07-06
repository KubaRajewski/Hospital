package com.example.zajecia7doktorki.controller;

import com.example.zajecia7doktorki.command.AdminUpdateCommand;
import com.example.zajecia7doktorki.dto.ActionDTO;
import com.example.zajecia7doktorki.dto.AdminDTO;
import com.example.zajecia7doktorki.mapping.ActionMapper;
import com.example.zajecia7doktorki.mapping.AdminMapper;
import com.example.zajecia7doktorki.service.AdminService;
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
@Tag(name = "Admin controller", description = "Manage customers and appointments")
@RequestMapping("/api/v1/admins")
public class AdminController {

    private final AdminService adminService;
    private final AdminMapper adminMapper;
    private final ActionMapper actionMapper;

    @Operation(summary = "Get admin details", responses = {
            @ApiResponse(description = "Successful retrieval of admin details", responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AdminDTO.class)))})
    @GetMapping("/get")
    public ResponseEntity<AdminDTO> getAdmin() {
        return new ResponseEntity<>(adminMapper.adminEntityToDTO(adminService.getAdmin()), HttpStatus.OK);
    }


    @Operation(summary = "Get all admins", description = "Retrieve a paginated list of admins")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of admin list",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)))
    @GetMapping("/getAll")
    public ResponseEntity<Page<AdminDTO>> getAdmins(@PageableDefault Pageable pageable) {
        return new ResponseEntity<>(adminService.getAdmins(pageable)
                .map(adminMapper::adminEntityToDTO), HttpStatus.OK);
    }

    @Operation(summary = "Update admin details")
    @ApiResponse(responseCode = "200", description = "Successful update of admin details",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AdminDTO.class)))
    @PutMapping
    public ResponseEntity<AdminDTO> updateAdmin(@RequestBody AdminUpdateCommand adminUpdateCommand) {
        return new ResponseEntity<>(adminMapper.adminEntityToDTO(adminService.updateAdmin(adminUpdateCommand)), HttpStatus.OK);
    }

    @Operation(summary = "Delete admin")
    @ApiResponse(responseCode = "200", description = "Successful deletion of admin")
    @DeleteMapping("/customer/{id}")
    public HttpStatus deleteCustomer(@PathVariable("id") Long id) {
        adminService.deleteCustomer(id);
        return HttpStatus.OK;
    }

    @Operation(summary = "Delete an appointment")
    @ApiResponse(responseCode = "200", description = "Successful deletion of appointment")
    @DeleteMapping("/appointment/{id}")
    public HttpStatus deleteAppointment(@PathVariable("id") Long id) {
        adminService.deleteAppointment(id);
        return HttpStatus.OK;
    }

    @Operation(summary = "Lock a customer")
    @ApiResponse(responseCode = "200", description = "Customer successfully locked")
    @PutMapping("/lock/{id}")
    public HttpStatus lockCustomer(@PathVariable("id") Long id) {
        adminService.setLocked(id, true);
        return HttpStatus.OK;
    }

    @Operation(summary = "Unlock a customer")
    @ApiResponse(responseCode = "200", description = "Customer successfully unlocked")
    @PutMapping("/unlock/{id}")
    public HttpStatus unlockCustomer(@PathVariable("id") Long id) {
        adminService.setLocked(id, false);
        return HttpStatus.OK;
    }

    @Operation(summary = "Disable a customer")
    @ApiResponse(responseCode = "200", description = "Customer successfully disabled")
    @PutMapping("/disable/{id}")
    public HttpStatus disableCustomer(@PathVariable("id") Long id) {
        adminService.setEnabled(id, false);
        return HttpStatus.OK;
    }

    @Operation(summary = "Enable a customer")
    @ApiResponse(responseCode = "200", description = "Customer successfully enabled")
    @PutMapping("/enable/{id}")
    public HttpStatus enableCustomer(@PathVariable("id") Long id) {
        adminService.setEnabled(id, true);
        return HttpStatus.OK;
    }

    @Operation(summary = "Display actions")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved page of actions",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)))
    @GetMapping("/actions")
    public ResponseEntity<Page<ActionDTO>> displayActions(@PageableDefault Pageable pageable) {
        return new ResponseEntity<>(adminService.getActions(pageable)
                .map(actionMapper::actionEntityToDTO), HttpStatus.OK);
    }
}
