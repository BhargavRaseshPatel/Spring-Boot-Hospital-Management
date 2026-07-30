package com.bhargav.JPA.hospitalManagement.controller;

import com.bhargav.JPA.hospitalManagement.dto.InsuranceRequest;
import com.bhargav.JPA.hospitalManagement.dto.InsuranceResponse;
import com.bhargav.JPA.hospitalManagement.entity.Patient;
import com.bhargav.JPA.hospitalManagement.service.InsuranceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients/{patientId}/insurance")
@RequiredArgsConstructor
@Tag(name = "Insurance", description = "The one-to-one patient/insurance link, with orphan removal")
public class InsuranceController {

    private final InsuranceService insuranceService;

    @PutMapping
    @Operation(summary = "Assign or replace a patient's insurance")
    public InsuranceResponse assign(@PathVariable Long patientId,
                                    @Valid @RequestBody InsuranceRequest request) {
        Patient patient = insuranceService.assignInsuranceToPatient(request, patientId);
        return InsuranceResponse.from(patient.getInsurance());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a patient's insurance",
            description = "orphanRemoval deletes the insurance row rather than leaving it detached")
    public void remove(@PathVariable Long patientId) {
        insuranceService.dissociateInsuranceFromPatient(patientId);
    }
}
