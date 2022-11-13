package de.szut.lf8_project.integration;

import de.szut.lf8_project.FullIntegrationTest;
import de.szut.lf8_project.common.Errorcode;
import de.szut.lf8_project.domain.employee.Employee;
import de.szut.lf8_project.domain.project.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Die Update Project Methode sollte")
public class TestUpdateProject extends FullIntegrationTest {

    @Test
    @DisplayName("ein Projekt erfolgreich komplett updaten")
    void fullUpdate() throws Exception {
        Project project = createAndSaveDefaultProjectWithProjectLead();
        Employee newProjectLead = saveEmployeeInRemoteRepository(createDefaultEmployeeWithout0Id());
        String jsonUpdateBody = String.format("""
                {
                "projectName" : "Neuer Name",
                "projectDescription" : "Neue Beschreibung",
                "customerId" : 999,
                "customerContact" : "Der Neue",
                "projectLeadId" : %d,
                "startDate" : "2022-01-13",
                "plannedEndDate" : "2022-02-14",
                "actualEndDate" : "2022-03-15"
                }
                """, newProjectLead.getId().unbox());

        ResultActions result = mockMvc.perform(
                put("/api/v1/project/" + project.getProjectId().get().unbox())
                        .header("Authorization", jwt.jwt())
                        .content(jsonUpdateBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );
        Project projectFromDb = getProjectByIdFromDatabase(project.getProjectId().get());

        result.andExpect(status().isOk());
        assertThat(projectFromDb.getProjectName().unbox()).isEqualTo("Neuer Name");
        assertThat(projectFromDb.getProjectDescription().get().unbox()).isEqualTo("Neue Beschreibung");
        assertThat(projectFromDb.getProjectLead().getProjectLeadId().unbox()).isEqualTo(newProjectLead.getId().unbox());
        assertThat(projectFromDb.getCustomerContact().unbox()).isEqualTo("Der Neue");
        assertThat(projectFromDb.getCustomer().getCustomerId().unbox()).isEqualTo(999);
        assertThat(projectFromDb.getStartDate().get().unbox()).isEqualTo(LocalDate.of(2022,1,13));
        assertThat(projectFromDb.getPlannedEndDate().get().unbox()).isEqualTo(LocalDate.of(2022,2,14));
        assertThat(projectFromDb.getActualEndDate().get().unbox()).isEqualTo(LocalDate.of(2022,3,15));

    }

    @ParameterizedTest(name = "für das Feld {0}")
    @MethodSource
    @DisplayName("ein Projekt erfolgreich teilweise updaten")
    void partialUpdate(String fieldToUpdate, Object newValue, String jsonResultPath) throws Exception {
        Project project = createAndSaveDefaultProjectWithProjectLead();
        saveProjectInDatabase(project);
        String jsonUpdateBody = "{\""  + fieldToUpdate + "\": " + (newValue instanceof String ? "\"" + newValue + "\"" : newValue ) + "}";

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

    @Test
    @DisplayName("ein Projekt erfolgreich teilweise updaten (ProjektLead)")
    void partialUpdateProjektLead() throws Exception {
        Project project = createAndSaveDefaultProjectWithProjectLead();
        Employee employee = createDefaultEmployeeWithout0Id();
        employee = saveEmployeeInRemoteRepository(employee);
        saveProjectInDatabase(project);
        String jsonUpdateBody = "{\"projectLeadId\": " + employee.getId() + "}";

        ResultActions result = mockMvc.perform(
                put("/api/v1/project/" + project.getProjectId().get().unbox())
                        .header("Authorization", jwt.jwt())
                        .content(jsonUpdateBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectLead.projectLeadId", is(employee.getId().unbox().intValue())));

    }

    @Nested
    @DisplayName("eine Projekt nicht updaten")
    class TestDontUpdateProject {

        @Test
        @DisplayName("wenn das neue Startdatum nicht vor dem geplanten Enddatum liegt")
        void invalidStartdate() throws Exception {
            Project project = createAndSaveDefaultProjectWithProjectLead();
            LocalDate invalidStartDate = project.getPlannedEndDate().get().unbox().plus(10, ChronoUnit.DAYS);

            ResultActions result = mockMvc.perform(
                    put("/api/v1/project/" + project.getProjectId().get().unbox())
                            .header("Authorization", jwt.jwt())
                            .content("{\"startDate\":\"" + invalidStartDate + "\"}")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
            );

            result
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title", is(Errorcode.END_DATE_BEFORE_START.toString())));

        }

        @Test
        @DisplayName("wenn das neue tatsächliche Enddatum vor dem Startdatum liegt")
        void invalidActualEnddate() throws Exception {
            Project project = createAndSaveDefaultProjectWithProjectLead();
            LocalDate invalidEndDate = project.getActualEndDate().get().unbox().plus(10, ChronoUnit.DAYS);

            ResultActions result = mockMvc.perform(
                    put("/api/v1/project/" + project.getProjectId().get().unbox())
                            .header("Authorization", jwt.jwt())
                            .content("{\"startDate\":\"" + invalidEndDate + "\"}")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
            );

            result
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title", is(Errorcode.END_DATE_BEFORE_START.toString())));
        }

        @Test
        @DisplayName("wenn der neue Team Lead nicht existiert")
        void missingTeamLead() throws Exception {
            Project project = createAndSaveDefaultProjectWithProjectLead();

            ResultActions result = mockMvc.perform(
                    put("/api/v1/project/" + project.getProjectId().get().unbox())
                            .header("Authorization", jwt.jwt())
                            .content("{\"projectLeadId\":989898989}")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
            );

            result
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title", is(Errorcode.ENTITY_NOT_FOUND.toString())));

        }

        @Test
        @DisplayName("wenn die Anfrage nicht richtig strukturiert ist / der Body fehlt")
        void badRequest() throws Exception {
            ResultActions result = mockMvc.perform(
                    put("/api/v1/project/1" )
                            .header("Authorization", jwt.jwt())
                            .content("")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
            );

            result
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title", is(Errorcode.INVALID_REQUEST_PARAMETER.toString())));

        }

        @Test
        @DisplayName("wenn das Projekt nicht existiert")
        void notFound() throws Exception {
            ResultActions result = mockMvc.perform(
                    put("/api/v1/project/787878" )
                            .header("Authorization", jwt.jwt())
                            .content("{\"projectName\":\"Ein neuer Name\"}")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
            );

            result
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title", is(Errorcode.ENTITY_NOT_FOUND.toString())));

        }

        @Test
        @DisplayName("wenn die Anfrage nicht authentifiziert ist")
        void notAuthenticated() throws Exception {
            ResultActions result = mockMvc.perform(
                    put("/api/v1/project/1" )
                            .content("{\"projectName\":\"Ein neuer Name\"}")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
            );

            result.andExpect(status().is(401));
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
                        1337,
                        "$.customer.customerId"
                ),
                Arguments.of(
                        "customerContact",
                        "Der Neue",
                        "$.customerContact"
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
