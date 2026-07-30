package com.bhargav.JPA.hospitalManagement.dto;

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
        LocalDateTime createdAt
) {
    public static PatientResponse from(Patient patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getName(),
                patient.getBirthDate(),
                patient.getEmail(),
                patient.getGender(),
                patient.getBloodGroup(),
                patient.getCreatedAt()
        );
    }
}
