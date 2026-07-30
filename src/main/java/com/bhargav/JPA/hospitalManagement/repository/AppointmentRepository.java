package com.bhargav.JPA.hospitalManagement.repository;

import com.bhargav.JPA.hospitalManagement.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Without the ORDER BY, row order is whatever the database returns and the UI reshuffles
    // every time an appointment is updated.
    List<Appointment> findByPatientIdOrderByAppointmentTimeAsc(Long patientId);
}