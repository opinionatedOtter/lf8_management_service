package de.szut.lf8_project.integration;

import de.szut.lf8_project.FullIntegrationTest;
import de.szut.lf8_project.common.Errorcode;
import de.szut.lf8_project.domain.employee.Employee;
import de.szut.lf8_project.domain.employee.EmployeeId;
import de.szut.lf8_project.domain.employee.ProjectRole;
import de.szut.lf8_project.domain.project.Project;
import de.szut.lf8_project.domain.project.TeamMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("The add-employee method")
public class TestAddEmployeeToProject extends FullIntegrationTest {

    @Test
    @DisplayName("should successfully add an existing employee to a project")
    void shouldAdd() throws Exception {
        Project project = saveProjectInDatabase(createAndSaveDefaultProjectWithProjectLead());
        ProjectRole role = createAndSaveQualificationInRemoteRepository();
        Employee employee = createDefaultEmployeeWithRolesWith0Id(List.of(role));
        EmployeeId employeeId = saveEmployeeInRemoteRepository(employee).getId();
        String jsonRequestBody = String.format("""
                {
                "employeeId" : %d,
                "projectRole" : "%s"
                }
                """, employeeId.unbox(), role.unbox());

        ResultActions result = mockMvc.perform(
                post("/api/v1/project/" + project.getProjectId().get().unbox())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody)
                        .header("Authorization", jwt.jwt())
        );

        result
                .andExpect(jsonPath("$.teamMember", hasItem(hasEntry("employeeId", employeeId.unbox().intValue()))))
                .andExpect(jsonPath("$.teamMember", hasItem(hasEntry("projectRole", role.unbox()))))
                .andExpect(status().isOk());

        Project updatedProjectInDb = getProjectByIdFromDatabase(project.getProjectId().get());
        assertThat(updatedProjectInDb.getTeamMembers()).contains(new TeamMember(
                employeeId,
                role
        ));
    }

    @Nested
    @DisplayName("should not add an employee")
    class TestDontAddEmployeeToProject {

        @Test
        @DisplayName("when the employee doesn't exist")
        void employee404() throws Exception {
            Project project = createAndSaveDefaultProjectWithProjectLead();

            String jsonRequestBody = """
                    {
                    "employeeId" : 999999999,
                    "projectRole" : "Macher"
                    }
                    """;

            ResultActions result = mockMvc.perform(
                    post("/api/v1/project/" + project.getProjectId().get().unbox())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequestBody)
                            .header("Authorization", jwt.jwt())
            );

            result
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title", is(Errorcode.ENTITY_NOT_FOUND.toString())));
        }

        @Test
        @DisplayName("when the employee is currrently booked in another project")
        void alreadyPlanned() throws Exception {
            ProjectRole role = createAndSaveQualificationInRemoteRepository();
            Employee employee = createDefaultEmployeeWithRolesWith0Id(List.of(role));
            EmployeeId employeeId = saveEmployeeInRemoteRepository(employee).getId();
            Project project = createAndSaveDefaultProjectWithProjectLead();
            project.getTeamMembers().add(
                    new TeamMember(
                            employeeId,
                            role
                    )
            );
            saveProjectInDatabase(project);
            Project collidingProject = createAndSaveDefaultProjectWithProjectLead();

            String jsonRequestBody = String.format("""
                    {
                    "employeeId" : %d,
                    "projectRole" : "%s"
                    }
                    """, employeeId.unbox(), role.unbox());

            ResultActions result = mockMvc.perform(
                    post("/api/v1/project/" + collidingProject.getProjectId().get().unbox())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequestBody)
                            .header("Authorization", jwt.jwt())
            );

            result
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title", is(Errorcode.EMPLOYEE_UNAVAILABLE.toString())));

        }

        @Test
        @DisplayName("when he doesn't have the required role")
        void wrongRole() throws Exception {
            Project project = createAndSaveDefaultProjectWithProjectLead();
            Employee employee = createDefaultEmployeeWith0Id();
            EmployeeId employeeIdOhneSkill = saveEmployeeInRemoteRepository(employee).getId();
            ProjectRole role = createAndSaveQualificationInRemoteRepository();
            String jsonRequestBody = String.format("""
                    {
                    "employeeId" : %d,
                    "projectRole" : "%s"
                    }
                    """, employeeIdOhneSkill.unbox(), role.unbox());

            ResultActions result = mockMvc.perform(
                    post("/api/v1/project/" + project.getProjectId().get().unbox())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequestBody)
                            .header("Authorization", jwt.jwt())
            );

            result
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title", is(Errorcode.EMPLOYEE_UNSUITABLE.toString())));
        }

        @Test
        @DisplayName("when the project does not exist")
        void missingProject() throws Exception {
            String jsonRequestBody = """
                    {
                    "employeeId" : 95959595,
                    "projectRole" : "Egal"
                    }
                    """;

            ResultActions result = mockMvc.perform(
                    post("/api/v1/project/3847593")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequestBody)
                            .header("Authorization", jwt.jwt())
            );

            result
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title", is(Errorcode.ENTITY_NOT_FOUND.toString())));
        }

        @Test
        @DisplayName("when the request is invalid")
        void malformedRequest() throws Exception {
            String invalidJsonRequestBody = """
                    {
                    "employeeId" : 95959595
                    }
                    """;

            ResultActions result = mockMvc.perform(
                    post("/api/v1/project/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJsonRequestBody)
                            .header("Authorization", jwt.jwt())
            );

            result
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title", is(Errorcode.INVALID_REQUEST_PARAMETER.toString())));
        }

        @Test
        @DisplayName("when the request has no authentication")
        void unauthenticated() throws Exception {
            String jsonRequestBody = """
                    {
                    "employeeId" : 1,
                    "projectRole" : "Egal"
                    }
                    """;

            ResultActions result = mockMvc.perform(
                    post("/api/v1/project/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequestBody)
            );

            result.andExpect(status().is(401))
                    .andExpect(jsonPath("$.projectId").doesNotExist());
        }
    }

    @Nested
    @DisplayName("when the employee is already in the project")
    class TestModifyAddedEmployeeToProject {

        @Test
        @DisplayName("not change the employee")
        void identicalUpdate() throws Exception {
            ProjectRole role = createAndSaveQualificationInRemoteRepository();
            Employee employee = createDefaultEmployeeWithRolesWith0Id(List.of(role));
            EmployeeId employeeId = saveEmployeeInRemoteRepository(employee).getId();
            Project project = createAndSaveDefaultProjectWithProjectLead();
            project.getTeamMembers().add(new TeamMember(
                    employeeId,
                    role
            ));
            saveProjectInDatabase(project);
            String jsonRequestBody = String.format("""
                    {
                    "employeeId" : %d,
                    "projectRole" : "%s"
                    }
                    """, employeeId.unbox(), role.unbox());

            ResultActions result = mockMvc.perform(
                    post("/api/v1/project/" + project.getProjectId().get().unbox())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequestBody)
                            .header("Authorization", jwt.jwt())
            );

            result
                    .andExpect(jsonPath("$.teamMember[0].employeeId", is(employeeId.unbox().intValue())))
                    .andExpect(jsonPath("$.teamMember[0].projectRole", is(role.unbox())))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("change the role of the employee if valid")
        void updateDifferentRole() throws Exception {
            ProjectRole oldRole = createAndSaveQualificationInRemoteRepository();
            ProjectRole newRole = createAndSaveQualificationInRemoteRepository();
            Employee employee = saveEmployeeInRemoteRepository(createDefaultEmployeeWithRolesWith0Id(List.of(oldRole, newRole)));
            Project project = createAndSaveDefaultProjectWithProjectLead();
            project.getTeamMembers().add(new TeamMember(
                    employee.getId(),
                    oldRole
            ));
            saveProjectInDatabase(project);
            String jsonRequestBody = String.format("""
                    {
                    "employeeId" : %d,
                    "projectRole" : "%s"
                    }
                    """, employee.getId().unbox(), newRole.unbox());

            ResultActions result = mockMvc.perform(
                    post("/api/v1/project/" + project.getProjectId().get().unbox())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequestBody)
                            .header("Authorization", jwt.jwt())
            );

            result
                    .andExpect(jsonPath("$.teamMember[0].employeeId", is(employee.getId().unbox().intValue())))
                    .andExpect(jsonPath("$.teamMember[0].projectRole", is(newRole.unbox())))
                    .andExpect(status().isOk());
        }
    }
}
