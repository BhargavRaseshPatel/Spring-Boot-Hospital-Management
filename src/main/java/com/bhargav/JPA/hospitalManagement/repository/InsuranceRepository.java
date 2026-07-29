package com.bhargav.JPA.hospitalManagement.repository;

import com.bhargav.JPA.hospitalManagement.entity.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsuranceRepository extends JpaRepository<Insurance, Long> {
}