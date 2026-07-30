package com.bhargav.JPA.hospitalManagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hospitalManagementOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Hospital Management API")
                .version("v1")
                .description("""
                        Spring Boot 4 / Spring Data JPA reference API over a normalized hospital schema.

                        Models every JPA relationship type: one-to-one (patient/insurance, with orphan
                        removal), one-to-many (patient/appointments), many-to-many with a join table
                        (doctors/departments) and a self-referencing head-of-department link.

                        Errors are RFC 7807 problem responses. Try GET /api/patients/with-appointments
                        for the JOIN FETCH query that replaced N+1 lazy loading."""));
    }
}
