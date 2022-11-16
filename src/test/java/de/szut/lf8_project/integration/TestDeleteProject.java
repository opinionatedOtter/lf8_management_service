package de.szut.lf8_project.integration;

import de.szut.lf8_project.FullIntegrationTest;
import de.szut.lf8_project.domain.employee.Employee;
import de.szut.lf8_project.domain.employee.ProjectRole;
import de.szut.lf8_project.domain.project.Project;
import de.szut.lf8_project.domain.project.ProjectLeadId;
import de.szut.lf8_project.domain.project.TeamMember;
import de.szut.lf8_project.repository.RepositoryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Die DELETE Project by ID Rest-Methode")
public class TestDeleteProject extends FullIntegrationTest {

    @Test
    @DisplayName("sollte erfolgreich ein simples Projekt löschen")
    void deleteSimpleProject() throws Exception {
        Project project = createAndSaveDefaultProjectWithProjectLead();

        ResultActions result = mockMvc.perform(delete("/api/v1/project/" + project.getProjectId().get().unbox())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", jwt.jwt())
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        assertThrows(RepositoryException.class, () -> getProjectByIdFromDatabase(project.getProjectId().get()));
    }

    @Test
    @DisplayName("sollte erfolgreich ein komplexes Projekt löschen")
    void deleteProjectWithTeamMembers() throws Exception {
        Project project = createAndSaveDefaultProjectWithProjectLead();
        ProjectRole projectRole = createAndSaveQualificationInRemoteRepository();
        Employee employee = saveEmployeeInRemoteRepository(createDefaultEmployeeWithRolesWith0Id(List.of(projectRole)));
        project.getTeamMembers().add(new TeamMember(employee.getId(), projectRole));
        Project finalProject = saveProjectInDatabase(project);

        ResultActions result = mockMvc.perform(delete("/api/v1/project/" + project.getProjectId().get().unbox())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", jwt.jwt())
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        assertThrows(RepositoryException.class, () -> getProjectByIdFromDatabase(finalProject.getProjectId().get()));
    }

    @Test
    @DisplayName("sollte kein Projekt finden und daher auch nicht löschen")
    void notFound() throws Exception {
        ResultActions result = mockMvc.perform(delete("/api/v1/project/99999999")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", jwt.jwt())
        );

        result.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("sollte unparsbare Anfragen abblocken")
    void badRequest() throws Exception {
        ResultActions result = mockMvc.perform(delete("/api/v1/project/xyz")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", jwt.jwt())
        );

        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("sollte unauthentifizierte Anfragen abblocken")
    void notAuthorized() throws Exception {
        ResultActions result = mockMvc.perform(delete("/api/v1/project/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        result.andExpect(status().is(401));
    }
}