package com.bhargav.JPA.hospitalManagement.dto;

import com.bhargav.JPA.hospitalManagement.entity.Insurance;

import java.time.LocalDate;

public record InsuranceResponse(
        Long id,
        String policyNumber,
        String provider,
        LocalDate validUntil,
        Long patientId
) {
    public static InsuranceResponse from(Insurance insurance) {
        return new InsuranceResponse(
                insurance.getId(),
                insurance.getPolicyNumber(),
                insurance.getProvider(),
                insurance.getValidUntil(),
                insurance.getPatient() == null ? null : insurance.getPatient().getId()
        );
    }
}
