package de.szut.lf8_project.integration;

import de.szut.lf8_project.FullIntegrationTest;
import de.szut.lf8_project.common.Errorcode;
import de.szut.lf8_project.domain.customer.Customer;
import de.szut.lf8_project.domain.customer.CustomerId;
import de.szut.lf8_project.domain.employee.*;
import de.szut.lf8_project.domain.project.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("The GET all projects from employee method should")
public class TestGetAllProjectsOfEmployee extends FullIntegrationTest {

    @Test
    @DisplayName("successfully get all projects of an employee")
    void getAllProjects() throws Exception {
        Project project = createAndSaveDefaultProjectWithProjectLead();
        Employee projectLead = saveEmployeeInRemoteRepository(new Employee(
                new EmployeeId(3212L),
                new LastName(UUID.randomUUID().toString()),
                new FirstName("Testnutzer mit Qualifikation für Integrationstest"),
                new Street("Teststr."),
                new City("Bremen"),
                new Postcode("28282"),
                new Phonenumber("0111778899"),
                Collections.emptyList()));
        Project differentProject = saveProjectInDatabase(createDifferentProject(new ProjectLeadId(projectLead.getId().unbox())));
        ProjectRole projectRole = createAndSaveQualificationInRemoteRepository();
        Employee employee = saveEmployeeInRemoteRepository(createDefaultEmployeeWithRolesWith0Id(List.of(projectRole)));
        project.getTeamMembers().add(new TeamMember(employee.getId(), projectRole));
        differentProject.getTeamMembers().add(new TeamMember(employee.getId(), projectRole));
        saveProjectInDatabase(project);
        saveProjectInDatabase(differentProject);


        ResultActions result = mockMvc.perform(get("/api/v1/project/byEmployee/" + employee.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", jwt.jwt())
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(employee.getId().unbox()))
                .andExpect(jsonPath("$.projects").isArray())

                .andExpect(jsonPath("$.projects.[0].projectId").value(project.getProjectId().get().unbox()))
                .andExpect(jsonPath("$.projects[0].projectName").value(project.getProjectName().unbox()))
                .andExpect(jsonPath("$.projects[0].startDate").value(project.getStartDate().get().unbox().toString()))
                .andExpect(jsonPath("$.projects[0].plannedEndDate").value(project.getPlannedEndDate().get().unbox().toString()))
                .andExpect(jsonPath("$.projects[0].actualEndDate").value(project.getActualEndDate().get().unbox().toString()))
                .andExpect(jsonPath("$.projects[0].projectRole").value(projectRole.unbox()))

                .andExpect(jsonPath("$.projects[1].projectId").value(differentProject.getProjectId().get().unbox()))
                .andExpect(jsonPath("$.projects[1].projectName").value(differentProject.getProjectName().unbox()))
                .andExpect(jsonPath("$.projects[1].startDate").value(differentProject.getStartDate().get().unbox().toString()))
                .andExpect(jsonPath("$.projects[1].plannedEndDate").value(differentProject.getPlannedEndDate().get().unbox().toString()))
                .andExpect(jsonPath("$.projects[1].actualEndDate").value(differentProject.getActualEndDate().get().unbox().toString()))
                .andExpect(jsonPath("$.projects[1].projectRole").value(projectRole.unbox()))

                .andExpect(jsonPath("$.projects[2]").doesNotExist());
    }

    @Test
    @DisplayName("successfully get all projects of an employee even if projects are empty")
    void getAllProjectsEmpty() throws Exception {
        Employee employee = saveEmployeeInRemoteRepository(createDefaultEmployeeWith0Id());

        ResultActions result = mockMvc.perform(get("/api/v1/project/byEmployee/" + employee.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", jwt.jwt())
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(employee.getId().unbox()))
                .andExpect(jsonPath("$.projects").isArray())
                .andExpect(jsonPath("$.projects").isEmpty());
    }

    @Test
    @DisplayName("returns 400 if employee does not exist")
    void getAllProjectsNoEmployee() throws Exception {

        ResultActions result = mockMvc.perform(get("/api/v1/project/byEmployee/12345")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", jwt.jwt())
        );

        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is(Errorcode.ENTITY_NOT_FOUND.toString())));
    }

    @Test
    @DisplayName("returns 401 if not authorized")
    void getAllProjectsNotAuthorized() throws Exception {

        ResultActions result = mockMvc.perform(get("/api/v1/project/byEmployee/12345")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        result
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist());
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
