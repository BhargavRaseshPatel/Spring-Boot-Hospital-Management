package com.bhargav.JPA.hospitalManagement.service;

import org.springframework.stereotype.Service;

import com.bhargav.JPA.hospitalManagement.entity.Appointment;
import com.bhargav.JPA.hospitalManagement.entity.Doctor;
import com.bhargav.JPA.hospitalManagement.entity.Patient;
import com.bhargav.JPA.hospitalManagement.repository.AppointmentRepository;
import com.bhargav.JPA.hospitalManagement.repository.DoctorRepository;
import com.bhargav.JPA.hospitalManagement.repository.PatientRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public Appointment createNewAppointment(Appointment appointment, Long doctorId ,Long patientId){
        if(appointment.getId() != null) throw new IllegalArgumentException("New appointment must not already have an id");

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with id: " + doctorId));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + patientId));

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        patient.getAppointments().add(appointment);

        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment reAssignAppointmentToAnotherDoctor(Long appointmentId, Long doctorId){
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found with id: " + appointmentId));
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with id: " + doctorId));

        appointment.setDoctor(doctor);
        return appointment;
    }
}
