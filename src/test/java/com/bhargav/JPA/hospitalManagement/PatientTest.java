package com.bhargav.JPA.hospitalManagement;

import com.bhargav.JPA.hospitalManagement.dto.BloodGroupCountResponseEntity;
import com.bhargav.JPA.hospitalManagement.entity.Patient;
import com.bhargav.JPA.hospitalManagement.entity.type.BloodGroupType;
import com.bhargav.JPA.hospitalManagement.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

@SpringBootTest
public class PatientTest {

    @Autowired
    private PatientRepository patientRepository;

    @Test
    public void testPatientRepository() {
        List<Patient> patientList = patientRepository.findAllPatientWithAppointment();
        System.out.println(patientList);

    }

    @Test
    public void testTransactionMethods() {
        // Patient patient = patientRepository.findByName("Aarav Sharma");

        // List<Patient> patientList =
        // patientRepository.findByBirthDateOrEmail(LocalDate.of(1992, 3,5),
        // "sneha.rao@example.com");

        List<Patient> patientList = patientRepository.findByBloodGroup(BloodGroupType.A_POSITIVE);
        System.out.println(patientList);

        List<Patient> patientList1 = patientRepository.findAllPatients(PageRequest.of(0, 2, Sort.by("id")));
        System.out.println(patientList1);

        // List<Object[]> bloodGroupList = patientRepository.countEachBloodGroupType();

        // for(Object[] objects : bloodGroupList){
        // System.out.println(objects[0]+ " " + objects[1]);
        // }

        List<BloodGroupCountResponseEntity> bloodGroupList = patientRepository.countEachBloodGroupType();
        for (BloodGroupCountResponseEntity bloodGroupCountResponse : bloodGroupList) {
            System.out.println(bloodGroupCountResponse);
        }

        // int rowsUpdated = patientRepository.updateNameWithId("Aarav Sharma", 1L);

    }
}
