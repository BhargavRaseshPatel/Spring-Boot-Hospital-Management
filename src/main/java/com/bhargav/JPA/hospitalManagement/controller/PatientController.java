package com.bhargav.JPA.hospitalManagement.controller;

import com.bhargav.JPA.hospitalManagement.dto.BloodGroupCountResponseEntity;
import com.bhargav.JPA.hospitalManagement.dto.PatientRequest;
import com.bhargav.JPA.hospitalManagement.dto.PatientResponse;
import com.bhargav.JPA.hospitalManagement.dto.PatientWithAppointmentsResponse;
import com.bhargav.JPA.hospitalManagement.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Patient records, appointments and blood-group analytics")
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/{id}")
    @Operation(summary = "Get a patient by id")
    public PatientResponse getById(@PathVariable Long id) {
        return PatientResponse.from(patientService.getPatientById(id));
    }

    @GetMapping
    @Operation(summary = "List patients (paginated)", description = "Supports ?page=, ?size= and ?sort=field,dir")
    public PagedModel<PatientResponse> list(Pageable pageable) {
        return new PagedModel<>(patientService.getAllPatients(pageable).map(PatientResponse::from));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a patient")
    public PatientResponse create(@Valid @RequestBody PatientRequest request) {
        return PatientResponse.from(patientService.createPatient(request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a patient", description = "Cascades to the patient's appointments and insurance")
    public void delete(@PathVariable Long id) {
        patientService.deletePatient(id);
    }

    @GetMapping("/with-appointments")
    @Operation(summary = "List patients with their appointments in a single query",
            description = "Uses LEFT JOIN FETCH instead of lazy loading, so this is 1 SQL statement rather than N+1")
    public List<PatientWithAppointmentsResponse> listWithAppointments() {
        return patientService.getAllPatientsWithAppointments().stream()
                .map(PatientWithAppointmentsResponse::from)
                .toList();
    }

    @GetMapping("/blood-group-distribution")
    @Operation(summary = "Count patients per blood group",
            description = "JPQL GROUP BY with a constructor projection straight into a DTO")
    public List<BloodGroupCountResponseEntity> bloodGroupDistribution() {
        return patientService.getBloodGroupDistribution();
    }
}
