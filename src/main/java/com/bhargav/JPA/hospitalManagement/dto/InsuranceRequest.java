package com.bhargav.JPA.hospitalManagement.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record InsuranceRequest(

        @NotBlank(message = "policyNumber is required")
        @Size(max = 50, message = "policyNumber must be at most 50 characters")
        String policyNumber,

        @NotBlank(message = "provider is required")
        @Size(max = 100, message = "provider must be at most 100 characters")
        String provider,

        @NotNull(message = "validUntil is required")
        @Future(message = "validUntil must be a future date")
        LocalDate validUntil
) {
}
