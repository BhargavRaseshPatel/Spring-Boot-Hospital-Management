package com.bhargav.JPA.hospitalManagement.repository;

import com.bhargav.JPA.hospitalManagement.dto.BloodGroupCountResponseEntity;
import com.bhargav.JPA.hospitalManagement.entity.Patient;
import com.bhargav.JPA.hospitalManagement.entity.type.BloodGroupType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public interface PatientRepository extends JpaRepository<Patient, Long> {


    Patient findByName(String name);

    List<Patient> findByBirthDateOrEmail(LocalDate date,String email);

    @Query("SELECT p FROM Patient p where p.bloodGroup = ?1")
    List<Patient> findByBloodGroup(@Param("bloodGroup") BloodGroupType bloodGroup);

    @Query("SELECT p FROM Patient  p where p.birthDate > :birthDate")
    List<Patient> findByBornAfterDate(@Param("birthDate") LocalDate birthDate);

    @Query("select new com.bhargav.JPA.hospitalManagement.dto.BloodGroupCountResponseEntity(p.bloodGroup, Count(p)) from Patient p group by p.bloodGroup")
//    List<Object[]> countEachBloodGroupType();
    List<BloodGroupCountResponseEntity> countEachBloodGroupType();

    @Query(value = "select * from patient_tbl", nativeQuery = true)
    List<Patient> findALlPatients(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Patient p SET p.name=:name where p.id = :id")
    int updateNameWithId(@Param("name") String name, @Param("id") Long id);

}
