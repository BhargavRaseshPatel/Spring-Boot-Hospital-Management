package com.bhargav.JPA.hospitalManagement.dto;

import com.bhargav.JPA.hospitalManagement.entity.Patient;

import java.util.List;

/**
 * Returned by the JOIN FETCH endpoint — the whole graph is loaded in a single query,
 * so mapping the appointments here never triggers a lazy load.
 */
public record PatientWithAppointmentsResponse(
        Long id,
        String name,
        String email,
        List<AppointmentResponse> appointments
) {
    public static PatientWithAppointmentsResponse from(Patient patient) {
        return new PatientWithAppointmentsResponse(
                patient.getId(),
                patient.getName(),
                patient.getEmail(),
                patient.getAppointments().stream().map(AppointmentResponse::from).toList()
        );
    }
}
