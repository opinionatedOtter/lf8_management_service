package de.szut.lf8_project.integration;

import de.szut.lf8_project.FullIntegrationTest;
import de.szut.lf8_project.common.Errorcode;
import de.szut.lf8_project.domain.employee.Employee;
import de.szut.lf8_project.domain.employee.ProjectRole;
import de.szut.lf8_project.domain.project.Project;
import de.szut.lf8_project.domain.project.TeamMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("The remove employee from project method")
public class TestRemoveEmployeeFromProject extends FullIntegrationTest {

    @Test
    @DisplayName("should remove an employee from a project")
    void shouldRemove() throws Exception {
        Project project = createAndSaveDefaultProjectWithProjectLead();
        Employee employee = saveEmployeeInRemoteRepository(createDefaultEmployeeWith0Id());
        project.getTeamMembers().add(new TeamMember(
               employee.getId(),
               new ProjectRole("egal")
        ));
        project = saveProjectInDatabase(project);

        ResultActions result = mockMvc.perform(
                delete("/api/v1/project/" + project.getProjectId().get().unbox() + "/removeEmployee/" + employee.getId().unbox())
                        .header("Authorization", jwt.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk());
        assertThat(getProjectByIdFromDatabase(project.getProjectId().get()).getTeamMembers()).isEmpty();

    }

    @Nested
    @DisplayName("should not remove an employee")
    class TestDontRemoveEmployeeFromProject {

        @Test
        @DisplayName("when the project does not exist")
        void projectNotFound() throws Exception {

            ResultActions result = mockMvc.perform(delete("/api/v1/project/868668/removeEmployee/2323")
                    .header("Authorization", jwt.jwt())
                    .contentType(MediaType.APPLICATION_JSON)
            );

            result
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title", is(Errorcode.ENTITY_NOT_FOUND.toString())));

        }

        @Test
        @DisplayName("when the employee is not in the project")
        void employeeNotInProject() throws Exception {
            Project project = createAndSaveDefaultProjectWithProjectLead();

            ResultActions result = mockMvc.perform(delete("/api/v1/project/" + project.getProjectId().get().unbox() + "/removeEmployee/2323")
                    .header("Authorization", jwt.jwt())
                    .contentType(MediaType.APPLICATION_JSON)
            );

            result
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title", is(Errorcode.ENTITY_NOT_FOUND.toString())));

        }

        @Test
        @DisplayName("when the requests has invalid parameters")
        void badRequest() throws Exception {
            Project project = createAndSaveDefaultProjectWithProjectLead();

            ResultActions result = mockMvc.perform(
                    delete("/api/v1/project/xyz/removeEmployee/xyz")
                            .header("Authorization", jwt.jwt())
                            .contentType(MediaType.APPLICATION_JSON)
            );

            result
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title", is(Errorcode.INVALID_REQUEST_PARAMETER.toString())));

        }

        @Test
        @DisplayName("when the request is not authorized")
        void notAuthorized() throws Exception {

            ResultActions result = mockMvc.perform(delete("/api/v1/project/xyz/removeEmployee/abc"));

            result.andExpect(status().is(401));

        }
    }


}
