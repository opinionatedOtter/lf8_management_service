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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Die get employees by project id-Methode")
public class TestGetEmployeesByProjectId extends FullIntegrationTest {

    @Test
    @DisplayName("sollte erfolgreich die Mitarbeiter zurückgeben")
    void getEmployees() throws Exception {
        Project project = createAndSaveDefaultProjectWithProjectLead();
        Employee employee = createDefaultEmployeeWith0Id();
        project.getTeamMembers().add(new TeamMember(employee.getId(), new ProjectRole("Some Role")));
        saveProjectInDatabase(project);

        ResultActions result = mockMvc.perform(get("/api/v1/employee/byProject/" + project.getProjectId().get().unbox())
                .header("Authorization", jwt.jwt())
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(project.getProjectId().get().unbox()))
                .andExpect(jsonPath("$.projectName").value(project.getProjectName().unbox()))
                .andExpect(jsonPath("$.teamMember[0].employeeId").value(project.getTeamMembers().stream().toList().get(0).getEmployeeId().unbox()));
    }


    @Test
    @DisplayName("sollte kein Projekt finden")
    void notFound() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/v1/employee/byProject/99999999")
                .header("Authorization", jwt.jwt())
        );

        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is(Errorcode.ENTITY_NOT_FOUND.toString())));
    }

    @Test
    @DisplayName("sollte unparsbare Anfragen abblocken")
    void badRequest() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/v1/employee/byProject/xyz")
                .header("Authorization", jwt.jwt())
        );

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(Errorcode.INVALID_REQUEST_PARAMETER.toString())));
    }

    @Test
    @DisplayName("sollte unauthentifizierte Anfragen abblocken")
    void notAuthorized() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/v1/employee/byProject/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        result.andExpect(status().is(401));
    }
}