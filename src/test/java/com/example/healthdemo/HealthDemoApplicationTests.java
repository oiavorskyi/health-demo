package com.example.healthdemo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HealthDemoApplicationTests {

    public static final String DOWN_STATUS_JSON = "{\"status\": \"DOWN\"}";
    public static final String UP_STATUS_JSON = "{\"status\": \"UP\"}";
    public static final String OUT_OF_SERVICE_STATUS_JSON = "{\"status\": \"OUT_OF_SERVICE\"}";

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void resetDependencyState() {
        Dependency.isUp = true;
    }

    @Test
    void shouldExposeLivenessProbe() throws Exception {
        mockMvc.perform(get("/actuator/health/liveness"))
                .andExpect(status().isOk())
                .andExpect(content().json(UP_STATUS_JSON));
    }

    @Test
    void shouldCustomizeLivenessProbe() throws Exception {
        // First emulate some "disaster" event in the application by
        // hitting custom endpoint
        mockMvc.perform(get("/emulate-liveness-issue"));
        mockMvc.perform(get("/actuator/health/liveness"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().json(DOWN_STATUS_JSON));

        // Then emulate recovery from the "disaster"
        mockMvc.perform(get("/emulate-liveness-recovery"));
        mockMvc.perform(get("/actuator/health/liveness"))
                .andExpect(status().isOk())
                .andExpect(content().json(UP_STATUS_JSON));
    }

    @Test
    void shouldExposeReadinessProbe() throws Exception {
        mockMvc.perform(get("/actuator/health/readiness"))
                .andExpect(status().isOk())
                .andExpect(content().json(UP_STATUS_JSON));
    }

    @Test
    void shouldCustomizeReadinessProbe() throws Exception {
        // First emulate some temporary issue in the application by
        // hitting custom endpoint
        mockMvc.perform(get("/emulate-readiness-issue"));
        mockMvc.perform(get("/actuator/health/readiness"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().json(OUT_OF_SERVICE_STATUS_JSON));

        // Then emulate recovery from the temporary issue
        mockMvc.perform(get("/emulate-readiness-recovery"));
        mockMvc.perform(get("/actuator/health/readiness"))
                .andExpect(status().isOk())
                .andExpect(content().json(UP_STATUS_JSON));
    }

    @Test
    void shouldIncludeExternalDependencyInReadinessProbe() throws Exception {
        // First emulate temporary unavailability of some external dependency
        // hitting custom endpoint
        mockMvc.perform(get("/emulate-dependency-down"));
        mockMvc.perform(get("/actuator/health/readiness"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().json(DOWN_STATUS_JSON));

        // Then emulate that the dependency is up again
        mockMvc.perform(get("/emulate-dependency-up"));
        mockMvc.perform(get("/actuator/health/readiness"))
                .andExpect(status().isOk())
                .andExpect(content().json(UP_STATUS_JSON));
    }

    @Test
    void shouldExposeExternalDependencyStatusInHealthCheck() throws Exception {
        // First emulate temporary unavailability of some external dependency
        // hitting custom endpoint
        mockMvc.perform(get("/emulate-dependency-down"));
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().json(DOWN_STATUS_JSON));

        // Then emulate that the dependency is up again
        mockMvc.perform(get("/emulate-dependency-up"));
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(content().json(UP_STATUS_JSON));
    }

}
