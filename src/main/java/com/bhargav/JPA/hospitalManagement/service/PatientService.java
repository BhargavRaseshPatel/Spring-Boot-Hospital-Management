package com.bhargav.JPA.hospitalManagement.service;

import com.bhargav.JPA.hospitalManagement.entity.Patient;
import com.bhargav.JPA.hospitalManagement.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    @Transactional
    public Patient getPatientById(Long id){

        Patient p1 = patientRepository.findById(id).orElseThrow();
        return p1;
    }
}
