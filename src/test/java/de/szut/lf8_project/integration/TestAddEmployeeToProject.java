package de.szut.lf8_project.integration;

import de.szut.lf8_project.FullIntegrationTest;
import de.szut.lf8_project.common.Errorcode;
import de.szut.lf8_project.common.Statuscode;
import de.szut.lf8_project.domain.employee.EmployeeId;
import de.szut.lf8_project.domain.employee.ProjectRole;
import de.szut.lf8_project.domain.project.Project;
import de.szut.lf8_project.repository.RepositoryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Der Add-Employee Endpunkt")
public class TestAddEmployeeToProject extends FullIntegrationTest {

    @Test
    @DisplayName("sollte einen vorhandenen Mitarbeiter erfolgreich hinzufügen")
    void shouldAdd() throws Exception {
        Project project = createProjectInDatabase();
        ProjectRole role = createQualificationInRemoteRepository();
        EmployeeId employeeId = createEmployeeWithSkillInRemoteRepository(role);
        String jsonRequestBody = String.format("""
                {
                "employeeId" : %d,
                "projectRoles" : "%s"
                }
                """, employeeId.unbox(), role.unbox());

        ResultActions result = mockMvc.perform(
                post("/api/v1/project/"+ project.getProjectId().get().unbox())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody)
                        .header("Authorization", jwt.jwt())
        );

        result

                .andExpect(jsonPath("$.teamMember[0].employeeId", is(employeeId.unbox().intValue())))
                .andExpect(jsonPath("$.teamMember[0].projectRole", is(role.unbox())))
                .andExpect(status().isCreated());
    }

    @Nested
    @DisplayName("sollte einen Mitarbeiter nicht hinzufügen")
    class TestDontAddEmployeeToProject {

        @Test
        @DisplayName("wenn dieser nicht existiert")
        void employee404() {
            fail();
        }

        @Test
        @DisplayName("er zu diesem Zeitpunkt bereits in einem anderen Projekt ist")
        void alreadyPlanned() throws Exception {
            ProjectRole role = createQualificationInRemoteRepository();
            EmployeeId employeeId = createEmployeeWithSkillInRemoteRepository(role);
            createProjectInDatabaseWithTeamMember(employeeId);
            Project collidingProject = createProjectInDatabase();

            String jsonRequestBody = String.format("""
                {
                "employeeId" : %d,
                "projectRoles" : "%s"
                }
                """, employeeId.unbox(), role.unbox());

            ResultActions result = mockMvc.perform(
                    post("/api/v1/project/"+ collidingProject.getProjectId().get().unbox())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequestBody)
                            .header("Authorization", jwt.jwt())
            );

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title", is(Errorcode.EMPLOYEE_UNAVAILABLE.toString())));

        }

        @Test
        @DisplayName("wenn er die gefragte Rolle nicht hat")
        void wrongRole() throws Exception {
            Project project = createProjectInDatabase();
            EmployeeId employeeIdOhneSkill = createEmployeeInRemoteRepository();
            ProjectRole role = createQualificationInRemoteRepository();
            String jsonRequestBody = String.format("""
                {
                "employeeId" : %d,
                "projectRoles" : "%s"
                }
                """, employeeIdOhneSkill.unbox(), role.unbox());

            ResultActions result = mockMvc.perform(
                    post("/api/v1/project/"+ project.getProjectId().get().unbox())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequestBody)
                            .header("Authorization", jwt.jwt())
            );

            result
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("das Projekt nicht vorhanden ist")
        void missingProject() {
            fail();
        }

        @Test
        @DisplayName("die Anfrage invalide ist")
        void malformedRequest() {
            fail();
        }

        @Test
        @DisplayName("die Anfrage nicht authentifiziert ist")
        void unauthenticated() {
            fail();
        }
    }

    @Nested
    @DisplayName("sollte einen vorhanden Mitarbeiter") // ja...?
    class TestModifyAddedEmployeeToProject {

        @Test
        @DisplayName("nicht verändern wenn alle Daten identisch sind")
        void identicalUpdate() {
            fail();
        }

        @Test
        @DisplayName("die Rolle updaten, wenn neu und valide")
        void updateDifferentRole() {
            fail();
        }
    }
}
