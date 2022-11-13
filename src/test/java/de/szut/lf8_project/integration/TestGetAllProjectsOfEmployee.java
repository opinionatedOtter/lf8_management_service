package de.szut.lf8_project.integration;

import de.szut.lf8_project.FullIntegrationTest;
import de.szut.lf8_project.common.Errorcode;
import de.szut.lf8_project.domain.employee.Employee;
import de.szut.lf8_project.domain.employee.ProjectRole;
import de.szut.lf8_project.domain.project.Project;
import de.szut.lf8_project.domain.project.TeamMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("The GET all projects from employee route should")
public class TestGetAllProjectsOfEmployee extends FullIntegrationTest {

    @Test
    @DisplayName("successfully get all projects of an employee")
    void getAllProjects() throws Exception {
        Project project = createAndSaveDefaultProjectWithProjectLead();
        Project differentProject = createAndSaveDifferentProjectWithProjectLead();
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
                .andExpect(jsonPath("$.projects[1].projectRole").value(projectRole.unbox()));
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
    @DisplayName("returns 403 if not authorized")
    void getAllProjectsNotAuthorized() throws Exception {

        ResultActions result = mockMvc.perform(get("/api/v1/project/byEmployee/12345")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        result
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist());
    }


}
