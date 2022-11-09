package de.szut.lf8_project.integration;

import de.szut.lf8_project.FullIntegrationTest;
import de.szut.lf8_project.domain.project.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Der get Project by ID Rest-Methode")
public class TestGetProjectById extends FullIntegrationTest {

    @Test
    @DisplayName("sollte erfolgreich ein Projekt zurückgeben")
    void getProject() throws Exception {
        Project project = createProjectInDatabase();

        ResultActions result = mockMvc.perform(get("/api/v1/project/" + project.getProjectId().get().unbox())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", jwt.jwt())
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(project.getProjectId().get().unbox()))
                .andExpect(jsonPath("$.projectName").value(project.getProjectName().unbox()))
                .andExpect(jsonPath("$.projectLead.projectLeadId").value(project.getProjectLead().getProjectLeadId().unbox()))
                .andExpect(jsonPath("$.customer.customerId").value(project.getCustomer().getCustomerId().unbox()))
                .andExpect(jsonPath("$.customerContact").value(project.getCustomerContact().unbox()))
                .andExpect(jsonPath("$.startDate").value(project.getStartDate().get().unbox().toString()))
                .andExpect(jsonPath("$.plannedEndDate").value(project.getPlannedEndDate().get().unbox().toString()))
                .andExpect(jsonPath("$.actualEndDate").value(project.getActualEndDate().get().unbox().toString()))
                .andExpect(jsonPath("$.teamMember").isEmpty());
    }


    @Test
    @DisplayName("sollte kein Projekt finden")
    void notFound() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/v1/project/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", jwt.jwt())
        );

        result.andExpect(status().isNotFound());
    }
}