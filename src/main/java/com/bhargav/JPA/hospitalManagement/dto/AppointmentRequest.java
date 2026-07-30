package com.bhargav.JPA.hospitalManagement.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AppointmentRequest(

        @NotNull(message = "appointmentTime is required")
        @Future(message = "appointmentTime must be in the future")
        LocalDateTime appointmentTime,

        @Size(max = 500, message = "reason must be at most 500 characters")
        String reason,

        @NotNull(message = "doctorId is required")
        Long doctorId,

        @NotNull(message = "patientId is required")
        Long patientId
) {
}
