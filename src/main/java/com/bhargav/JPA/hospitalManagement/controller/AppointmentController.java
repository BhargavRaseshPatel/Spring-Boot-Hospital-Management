package com.bhargav.JPA.hospitalManagement.controller;

import com.bhargav.JPA.hospitalManagement.dto.AppointmentRequest;
import com.bhargav.JPA.hospitalManagement.dto.AppointmentResponse;
import com.bhargav.JPA.hospitalManagement.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Booking appointments and reassigning them between doctors")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/{id}")
    @Operation(summary = "Get an appointment by id")
    public AppointmentResponse getById(@PathVariable Long id) {
        return AppointmentResponse.from(appointmentService.getAppointmentById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Book an appointment for a patient with a doctor")
    public AppointmentResponse create(@Valid @RequestBody AppointmentRequest request) {
        return AppointmentResponse.from(appointmentService.createNewAppointment(request));
    }

    @PatchMapping("/{id}/doctor/{doctorId}")
    @Operation(summary = "Reassign an appointment to another doctor")
    public AppointmentResponse reassign(@PathVariable Long id, @PathVariable Long doctorId) {
        return AppointmentResponse.from(appointmentService.reAssignAppointmentToAnotherDoctor(id, doctorId));
    }

    @GetMapping(params = "patientId")
    @Operation(summary = "List a patient's appointments")
    public List<AppointmentResponse> listForPatient(@RequestParam Long patientId) {
        return appointmentService.getAppointmentsForPatient(patientId).stream()
                .map(AppointmentResponse::from)
                .toList();
    }
}
