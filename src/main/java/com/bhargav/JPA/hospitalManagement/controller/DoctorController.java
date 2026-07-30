package com.bhargav.JPA.hospitalManagement.controller;

import com.bhargav.JPA.hospitalManagement.dto.DoctorResponse;
import com.bhargav.JPA.hospitalManagement.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctors", description = "Read-only doctor directory")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    @Operation(summary = "List all doctors")
    public List<DoctorResponse> list() {
        return doctorService.getAllDoctors().stream().map(DoctorResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a doctor by id")
    public DoctorResponse getById(@PathVariable Long id) {
        return DoctorResponse.from(doctorService.getDoctorById(id));
    }
}
