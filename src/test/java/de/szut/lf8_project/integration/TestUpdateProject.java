package de.szut.lf8_project.integration;

import de.szut.lf8_project.FullIntegrationTest;
import de.szut.lf8_project.domain.project.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Die Update Project Methode sollte")
public class TestUpdateProject extends FullIntegrationTest {

    @Test
    @DisplayName("ein Projekt erfolgreich komplett updaten")
    void fullUpdate() throws Exception {
        Project project = ???;
        String jsonUpdateBody = "";

        ResultActions result = mockMvc.perform(
                put("/api/v1/project/" + project.getProjectId().get().unbox())
                        .header("Authorization", jwt.jwt())
                        .content(jsonUpdateBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

    }

    @ParameterizedTest(name = "für das Feld {0}")
    @MethodSource
    @DisplayName("ein Projekt erfolgreich teilweise updaten")
    void partialUpdate(String fieldToUpdate, String newValue, String jsonResultPath) throws Exception {
        Project project = ???;
        String jsonUpdateBody = "{\"" + fieldToUpdate + "\": \"" + newValue + "\"}";

        ResultActions result = mockMvc.perform(
                put("/api/v1/project/" + project.getProjectId().get().unbox())
                        .header("Authorization", jwt.jwt())
                        .content(jsonUpdateBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath(jsonResultPath, is(newValue)));

    }

    @Nested
    @DisplayName("eine Projekt nicht updaten")
    class TestDontUpdateProject {

        @Test
        @DisplayName("wenn das neue Startdatum nicht vor dem geplanten Enddatum liegt")
        void invalidStartdate() {

        }

        @Test
        @DisplayName("wenn das neue tatsächliche Enddatum vor dem Startdatum liegt")
        void invalidActualEnddate() {

        }

        @Test
        @DisplayName("wenn der neue Team Lead nicht existiert")
        void missingTeamLead() {

        }

        @Test
        @DisplayName("wenn die Anfrage nicht richtig strukturiert ist")
        void badRequest() {

        }

        @Test
        @DisplayName("wenn das Projekt nicht existiert")
        void notFound() {

        }

        @Test
        @DisplayName("wenn die Anfrage nicht authentifiziert ist")
        void notAuthenticated() {

        }
    }

    public static Stream<Arguments> partialUpdate() {
        return Stream.of(
                Arguments.of(
                        "projectName",
                        "ich bin der neue Name",
                        "$.projectName"
                ),
                Arguments.of(
                        "projectDescription",
                        "ich bin die neue Beschreibung",
                        "$.projectDescription"
                ),
                Arguments.of(
                        "customerId",
                        "1337",
                        "$.customer.customerId"
                ),
                Arguments.of(
                        "customerContact",
                        "Der Neue",
                        "$.customerContact"
                ),
                Arguments.of(
                        "projectLeadId",
                        "hier eine echte Nummer?!",
                        "$.projectLead.projectLeadId"
                ),
                Arguments.of(
                        "startDate",
                        "2020-01-01",
                        "$.startDate"
                ),
                Arguments.of(
                        "plannedEndDate",
                        "2023-02-02",
                        "$.plannedEndDate"
                ),
                Arguments.of(
                        "actualEndDate",
                        "2024-03-03",
                        "$.actualEndDate"
                )
        );
    }
}
