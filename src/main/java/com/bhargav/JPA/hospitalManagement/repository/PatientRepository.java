package com.bhargav.JPA.hospitalManagement.repository;

import com.bhargav.JPA.hospitalManagement.dto.BloodGroupCountResponseEntity;
import com.bhargav.JPA.hospitalManagement.entity.Patient;
import com.bhargav.JPA.hospitalManagement.entity.type.BloodGroupType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {


    Patient findByName(String name);

    List<Patient> findByBirthDateOrEmail(LocalDate date,String email);

    @Query("SELECT p FROM Patient p where p.bloodGroup = :bloodGroup")
    List<Patient> findByBloodGroup(@Param("bloodGroup") BloodGroupType bloodGroup);

    @Query("SELECT p FROM Patient p where p.birthDate > :birthDate")
    List<Patient> findByBornAfterDate(@Param("birthDate") LocalDate birthDate);

    @Query("select new com.bhargav.JPA.hospitalManagement.dto.BloodGroupCountResponseEntity(p.bloodGroup, Count(p)) from Patient p group by p.bloodGroup")
//    List<Object[]> countEachBloodGroupType();
    List<BloodGroupCountResponseEntity> countEachBloodGroupType();

    @Query(value = "select * from patient_tbl", nativeQuery = true)
    List<Patient> findAllPatients(Pageable pageable);

    // clear/flush keep the persistence context from serving a stale entity after the bulk update
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Patient p SET p.name=:name where p.id = :id")
    int updateNameWithId(@Param("name") String name, @Param("id") Long id);

    // DISTINCT: a JOIN FETCH on a to-many returns one row per appointment, duplicating the parent.
    // The nested fetch of a.doctor matters too — Appointment.doctor is a @ManyToOne (EAGER by
    // default), so without it Hibernate issues one extra SELECT per doctor.
    @Query("""
            SELECT DISTINCT p FROM Patient p
            LEFT JOIN FETCH p.appointments a
            LEFT JOIN FETCH a.doctor
            """)
    List<Patient> findAllPatientWithAppointment();

}
