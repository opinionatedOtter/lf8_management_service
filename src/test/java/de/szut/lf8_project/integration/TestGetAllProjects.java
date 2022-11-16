package de.szut.lf8_project.integration;

import de.szut.lf8_project.FullIntegrationTest;
import de.szut.lf8_project.domain.customer.Customer;
import de.szut.lf8_project.domain.customer.CustomerId;
import de.szut.lf8_project.domain.employee.Employee;
import de.szut.lf8_project.domain.employee.ProjectRole;
import de.szut.lf8_project.domain.project.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("The all-projects rest method")
public class TestGetAllProjects extends FullIntegrationTest {

    @Test
    @DisplayName("should show all projects")
    void getAllProjects() throws Exception {
        Project project1 = createAndSaveDefaultProjectWithProjectLead();

        Project tmp_project = saveProjectInDatabase(createDifferentProject(
                new ProjectLeadId(project1.getProjectLead().getProjectLeadId().unbox())));

        ProjectRole projectRole = createAndSaveQualificationInRemoteRepository();
        Employee employee = saveEmployeeInRemoteRepository(createDefaultEmployeeWithRolesWith0Id(List.of(projectRole)));
        tmp_project.getTeamMembers().add(new TeamMember(employee.getId(), projectRole));
        Project project2 = saveProjectInDatabase(tmp_project);

        ResultActions result = mockMvc.perform(get("/api/v1/project")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", jwt.jwt())
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").isNotEmpty())
                .andExpect(jsonPath("$[1]").isNotEmpty())
                .andExpect(jsonPath("$[0].projectId").isNotEmpty())
                .andExpect(jsonPath("$[1].projectId").isNotEmpty())
                .andExpect(jsonPath("$[0].projectName").value(project1.getProjectName().unbox()))
                .andExpect(jsonPath("$[1].projectName").value(project2.getProjectName().unbox()))
                .andExpect(jsonPath("$[0].teamMember").isEmpty())
                .andExpect(jsonPath("$[1].teamMember").isNotEmpty());
    }

    @Test
    @DisplayName("should block unauthorized requests")
    void notAuthorized() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/v1/project")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        result
                .andExpect(status().is(401))
                .andExpect(jsonPath("$[0]").doesNotExist())
                .andExpect(jsonPath("$[0].projectId").doesNotExist());
    }

    private Project createDifferentProject(final ProjectLeadId projectLeadId) {
        return Project.builder()
                .projectId(Optional.empty())
                .projectName(new ProjectName("Different"))
                .projectDescription(Optional.of(new ProjectDescription("Unique")))
                .projectLead(new ProjectLead(projectLeadId))
                .customer(new Customer(new CustomerId(69L)))
                .customerContact(new CustomerContact("Freundschaft mit Franz-Ferdinand Falke endet"))
                .startDate(Optional.of(new StartDate(LocalDate.of(2023, 1, 20))))
                .plannedEndDate(Optional.of(new PlannedEndDate(LocalDate.of(2023, 4, 24))))
                .actualEndDate(Optional.of(new ActualEndDate(LocalDate.of(2023, 6, 26))))
                .teamMembers(Collections.emptySet())
                .build();
    }

}