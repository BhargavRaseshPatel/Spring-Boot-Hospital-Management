package com.bhargav.JPA.hospitalManagement.repository;

import com.bhargav.JPA.hospitalManagement.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}