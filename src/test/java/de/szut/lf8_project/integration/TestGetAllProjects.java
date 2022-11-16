package de.szut.lf8_project.integration;

import de.szut.lf8_project.FullIntegrationTest;
import de.szut.lf8_project.domain.project.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("The all-projects rest method")
public class TestGetAllProjects extends FullIntegrationTest {

    @Test
    @DisplayName("should show all projects")
    void getAllProjects() throws Exception {
        // Project project = createProjectInDatabase();

        ResultActions result = mockMvc.perform(get("/api/v1/project")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", jwt.jwt())
        );

        result
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should block unauthorized requests")
    void notAuthorized() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/v1/project")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        result.andExpect(status().is(401));
    }
}