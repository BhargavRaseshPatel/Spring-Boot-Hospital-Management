package com.bhargav.JPA.hospitalManagement.dto;

import com.bhargav.JPA.hospitalManagement.entity.type.BloodGroupType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PatientRequest(

        @NotBlank(message = "name is required")
        @Size(max = 40, message = "name must be at most 40 characters")
        String name,

        @NotNull(message = "birthDate is required")
        @Past(message = "birthDate must be in the past")
        LocalDate birthDate,

        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid address")
        String email,

        String gender,

        @NotNull(message = "bloodGroup is required")
        BloodGroupType bloodGroup
) {
}
