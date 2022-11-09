package de.szut.lf8_project.integration;

import de.szut.lf8_project.FullIntegrationTest;
import de.szut.lf8_project.domain.project.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Der get Project by ID Rest-Methode")
public class TestGetProjectById extends FullIntegrationTest {

    @Test
    @DisplayName("sollte erfolgreich ein Projekt zurückgeben")
    void getProject() throws Exception {
        Project project = createProjectInDatabase();
        System.out.println("Success");
    }
}