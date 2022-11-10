package de.szut.lf8_project.integration;

import de.szut.lf8_project.FullIntegrationTest;
import de.szut.lf8_project.domain.project.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Die GET All Projects Rest-Methode")
public class TestGetAllProjects extends FullIntegrationTest {

    @Test
    @DisplayName("sollte erfolgreich ein Projekt löschen")
    void deleteProject() throws Exception {
        // Project project = createProjectInDatabase();

        ResultActions result = mockMvc.perform(get("/api/v1/project/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", jwt.jwt())
        );

        result
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("sollte unparsbare Anfragen abblocken")
    void badRequest() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/v1/project/xyz")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", jwt.jwt())
        );

        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("sollte unauthentifizierte Anfragen abblocken")
    void notAuthorized() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/v1/project/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        result.andExpect(status().is(401));
    }
}