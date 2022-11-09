package de.szut.lf8_project.integration;

import de.szut.lf8_project.FullIntegrationTest;
import de.szut.lf8_project.domain.project.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Der get Project by ID Rest-Methode")
public class TestGetProjectById extends FullIntegrationTest {

    @Test
    @DisplayName("sollte erfolgreich ein Projekt zurückgeben")
    void getProject() throws Exception {
        Project project = createProjectInDatabase();

        ResultActions result = mockMvc.perform(get("/api/v1/" + project.getProjectId().get().unbox())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", jwt.jwt())
        );

        result.andExpect(status().isOk());
    }
}