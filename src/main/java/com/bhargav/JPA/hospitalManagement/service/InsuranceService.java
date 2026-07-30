package com.bhargav.JPA.hospitalManagement.service;

import com.bhargav.JPA.hospitalManagement.dto.InsuranceRequest;
import com.bhargav.JPA.hospitalManagement.entity.Insurance;
import com.bhargav.JPA.hospitalManagement.entity.Patient;
import com.bhargav.JPA.hospitalManagement.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InsuranceService {

    private final PatientRepository patientRepository;

    @Transactional
    public Patient assignInsuranceToPatient(InsuranceRequest request, Long patientId) {
        Insurance insurance = Insurance.builder()
                .policyNumber(request.policyNumber())
                .provider(request.provider())
                .validUntil(request.validUntil())
                .build();
        return assignInsuranceToPatient(insurance, patientId);
    }

    @Transactional
    public Patient assignInsuranceToPatient(Insurance insurance, Long patientId) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id : " + patientId));
        patient.setInsurance(insurance);
        insurance.setPatient(patient); // bidirectional consistency maintained
        return patient;
    }

    @Transactional
    public Patient dissociateInsuranceFromPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + patientId));

        patient.setInsurance(null); // orphanRemoval deletes the insurance row on flush
        return patient;
    }
}
