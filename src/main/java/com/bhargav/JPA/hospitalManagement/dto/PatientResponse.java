package com.bhargav.JPA.hospitalManagement.dto;

import com.bhargav.JPA.hospitalManagement.entity.Insurance;
import com.bhargav.JPA.hospitalManagement.entity.Patient;
import com.bhargav.JPA.hospitalManagement.entity.type.BloodGroupType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PatientResponse(
        Long id,
        String name,
        LocalDate birthDate,
        String email,
        String gender,
        BloodGroupType bloodGroup,
        LocalDateTime createdAt,
        // Flat, rather than a nested InsuranceResponse: that record reads insurance.getPatient(),
        // which would walk back across the association for every row in a list.
        String insuranceProvider,
        LocalDate insuranceValidUntil
) {
    public static PatientResponse from(Patient patient) {
        Insurance insurance = patient.getInsurance();
        return new PatientResponse(
                patient.getId(),
                patient.getName(),
                patient.getBirthDate(),
                patient.getEmail(),
                patient.getGender(),
                patient.getBloodGroup(),
                patient.getCreatedAt(),
                insurance == null ? null : insurance.getProvider(),
                insurance == null ? null : insurance.getValidUntil()
        );
    }
}
