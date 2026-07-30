package com.bhargav.JPA.hospitalManagement.service;

import com.bhargav.JPA.hospitalManagement.dto.BloodGroupCountResponseEntity;
import com.bhargav.JPA.hospitalManagement.dto.PatientRequest;
import com.bhargav.JPA.hospitalManagement.entity.Patient;
import com.bhargav.JPA.hospitalManagement.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    @Transactional(readOnly = true)
    public Patient getPatientById(Long id){
        return patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + id));
    }

    @Transactional
    public Patient createPatient(PatientRequest request) {
        Patient patient = new Patient();
        patient.setName(request.name());
        patient.setBirthDate(request.birthDate());
        patient.setEmail(request.email());
        patient.setGender(request.gender());
        patient.setBloodGroup(request.bloodGroup());
        return patientRepository.save(patient);
    }

    @Transactional(readOnly = true)
    public Page<Patient> getAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable);
    }

    /** Single query via LEFT JOIN FETCH — see PatientRepository.findAllPatientWithAppointment(). */
    @Transactional(readOnly = true)
    public List<Patient> getAllPatientsWithAppointments() {
        return patientRepository.findAllPatientWithAppointment();
    }

    @Transactional(readOnly = true)
    public List<BloodGroupCountResponseEntity> getBloodGroupDistribution() {
        return patientRepository.countEachBloodGroupType();
    }

    @Transactional
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new EntityNotFoundException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id); // cascades to appointments and insurance
    }
}
