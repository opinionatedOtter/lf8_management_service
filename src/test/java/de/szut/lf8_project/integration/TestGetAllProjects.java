package de.szut.lf8_project.integration;

import de.szut.lf8_project.FullIntegrationTest;
import de.szut.lf8_project.domain.employee.Employee;
import de.szut.lf8_project.domain.employee.ProjectRole;
import de.szut.lf8_project.domain.project.Project;
import de.szut.lf8_project.domain.project.TeamMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Die GET All Projects Rest-Methode")
public class TestGetAllProjects extends FullIntegrationTest {

    @Test
    @DisplayName("sollte alle Projekte anzeigen")
    void getAllProjects() throws Exception {
        Project project1 = createAndSaveDefaultProjectWithProjectLead();

        Project tmp_project = createAndSaveDefaultProjectWithProjectLead();

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
                .andExpect(jsonPath("$[0].projectName").value(project2.getProjectName().unbox()))
                .andExpect(jsonPath("$[1].projectName").value(project2.getProjectName().unbox()));
    }

    @Test
    @DisplayName("sollte unauthentifizierte Anfragen abblocken")
    void notAuthorized() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/v1/project")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        result
                .andExpect(status().is(401))
                .andExpect(jsonPath("$[0]").doesNotExist())
                .andExpect(jsonPath("$[0].projectId").doesNotExist());
    }
}