package com.bhargav.JPA.hospitalManagement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Guards the HTTP contract: status codes and the single RFC 7807 error shape. Every failure mode
 * must come back as a ProblemDetail — the framework ones bypassed the handler until
 * GlobalExceptionHandler started extending ResponseEntityExceptionHandler.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ApiContractTest {

    @Autowired
    private MockMvc mockMvc;

    // Asserts shape, not seed content — the names in data.sql are free to change.
    @Test
    void getsSeededPatient() throws Exception {
        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.email").isNotEmpty());
    }

    @Test
    void listsPatientsWithPageMetadata() throws Exception {
        mockMvc.perform(get("/api/patients").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page.size").value(2))
                .andExpect(jsonPath("$.page.totalElements").isNumber());
    }

    @Test
    void reportsFieldErrorsOnInvalidBody() throws Exception {
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"","birthDate":"2090-01-01","email":"not-an-email","bloodGroup":null}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.birthDate").exists())
                .andExpect(jsonPath("$.errors.bloodGroup").exists());
    }

    @Test
    void returnsProblemDetailForMissingEntity() throws Exception {
        mockMvc.perform(get("/api/patients/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Patient not found with id: 9999"));
    }

    /** Self-contained: creates the row it then collides with, so it survives edits to data.sql. */
    @Test
    void rejectsDuplicateEmailWithConflict() throws Exception {
        String body = """
                {"name":"Dup Check","birthDate":"1990-01-01","email":"dup.check@example.com",
                 "gender":"MALE","bloodGroup":"O_POSITIVE"}""";

        mockMvc.perform(post("/api/patients").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/patients").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void rejectsAppointmentInThePast() throws Exception {
        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"appointmentTime":"2020-01-01T10:00:00","reason":"x","doctorId":1,"patientId":1}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.appointmentTime").exists());
    }

    @Test
    void rejectsInsuranceThatHasAlreadyExpired() throws Exception {
        mockMvc.perform(put("/api/patients/1/insurance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"policyNumber":"OLD-1","provider":"X","validUntil":"2001-01-01"}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.validUntil").exists());
    }

    /** Framework-level failures must use the same ProblemDetail shape, not Spring's legacy body. */
    @Test
    void usesProblemDetailForFrameworkErrorsToo() throws Exception {
        mockMvc.perform(get("/api/appointments")) // patientId is required
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").doesNotExist()); // the legacy shape's marker field

        mockMvc.perform(put("/api/patients").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.error").doesNotExist());

        mockMvc.perform(get("/api/patients/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").doesNotExist());
    }
}
