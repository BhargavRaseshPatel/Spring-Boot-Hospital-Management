package com.bhargav.JPA.hospitalManagement.dto;

import com.bhargav.JPA.hospitalManagement.entity.Appointment;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        LocalDateTime appointmentTime,
        String reason,
        Long doctorId,
        String doctorName,
        Long patientId,
        String patientName
) {
    public static AppointmentResponse from(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getAppointmentTime(),
                appointment.getReason(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getName(),
                appointment.getPatient().getId(),
                appointment.getPatient().getName()
        );
    }
}
