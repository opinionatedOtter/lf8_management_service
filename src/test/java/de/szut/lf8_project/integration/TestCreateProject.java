package de.szut.lf8_project.integration;

import de.szut.lf8_project.FullIntegrationTest;
import de.szut.lf8_project.domain.employee.Employee;
import de.szut.lf8_project.domain.employee.EmployeeId;
import de.szut.lf8_project.domain.project.Project;
import de.szut.lf8_project.domain.project.ProjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Der Create Project Rest-Endpunkt")
public class TestCreateProject extends FullIntegrationTest {


    @Test
    @DisplayName("sollte erfolgreich ein Projekt erstellen")
    void createProject() throws Exception {
        Employee employee = createDefaultEmployeeWith0Id();
        EmployeeId newEmployee = saveEmployeeInRemoteRepository(employee).getId();
        String jsonBody = String.format("""
                 {
                        "projectName": "foobar",
                        "projectLeadId": %d,
                        "customerId": 789,
                        "customerContact": "Testkontakt",
                        "projectDescription": "foobar at the beach",
                        "startDate": "2022-09-23"
                        }
                """, newEmployee.unbox());

        ResultActions result = mockMvc.perform(post("/api/v1/project")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", jwt.jwt())
                .content(jsonBody)
        );
        ProjectId projectId = getProjectIdFromMvcJsonResponse(result.andReturn());

        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.projectId").isNotEmpty())
                .andExpect(jsonPath("$.projectName").value("foobar"))
                .andExpect(jsonPath("$.projectLead.projectLeadId").value(newEmployee.unbox()))
                .andExpect(jsonPath("$.customer.customerId").value(789))
                .andExpect(jsonPath("$.customerContact").value("Testkontakt"))
                .andExpect(jsonPath("$.startDate").value("2022-09-23"))
                .andExpect(jsonPath("$.plannedEndDate").isEmpty())
                .andExpect(jsonPath("$.actualEndDate").isEmpty())
                .andExpect(jsonPath("$.teamMember").isEmpty());
        Project savedProject = getProjectByIdFromDatabase(projectId);
        assertThat(savedProject.getProjectLead().getProjectLeadId().unbox()).isEqualTo(newEmployee.unbox());
        assertThat(savedProject.getProjectName().unbox()).isEqualTo("foobar");
        assertThat(savedProject.getProjectDescription().get().unbox()).isEqualTo("foobar at the beach");
        assertThat(savedProject.getStartDate().get().unbox()).isEqualTo(LocalDate.of(2022, 9, 23));
        assertThat(savedProject.getActualEndDate()).isEmpty();
        assertThat(savedProject.getPlannedEndDate()).isEmpty();
        assertThat(savedProject.getCustomer().getCustomerId().unbox()).isEqualTo(789);


    }

    @Test
    @DisplayName("einen 404 Fehler werfen wenn der angegebene Mitarbeiter nicht gefunden werden kann")
    void missingEmployee() throws Exception {
        ResultActions result = mockMvc.perform(post("/api/v1/project")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", jwt.jwt())
                .content("""
                         {
                                "projectName": "foobar",
                                "projectLeadId": 9999999999,
                                "customerId": 789,
                                "customerContact": "Testkontakt",
                                "projectDescription": "foobar at the beach",
                                "startDate": "2022-09-23"
                                }
                        """)
        );

        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.projectId").doesNotExist());
    }

    @Test
    @DisplayName("einen 401 Fehler werfen wenn die Authorisierung fehlschlägt")
    void noAuth() throws Exception {
        ResultActions result = mockMvc.perform(post("/api/v1/project")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "invalidjwt")
                .content("""
                         {
                                "projectName": "foobar",
                                "projectLeadId": 2,
                                "customerId": 789,
                                "customerContact": "Testkontakt",
                                "projectDescription": "foobar at the beach",
                                "startDate": "2022-09-23"
                                }
                        """)
        );

        result
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.projectId").doesNotExist());
    }

    @Test
    @DisplayName("einen 400 Fehler werfen wenn die Request invalide Parameter enhält")
    void invalidParameterBadRequest() throws Exception {
        ResultActions result = mockMvc.perform(post("/api/v1/project")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", jwt.jwt())
                .content("""
                         {
                                "projectName": "foobar",
                                "projectLeadId": notAnId,
                                "customerId": 789,
                                "customerContact": "Testkontakt",
                                "projectDescription": "foobar at the beach",
                                "startDate": "2022-09-23"
                                }
                        """)
        );

        result
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.projectId").doesNotExist());
    }

    @Test
    @DisplayName("einen 400 Fehler werfen wenn die Request nicht vollständig ist")
    void missingParameterBadRequest() throws Exception {
        ResultActions result = mockMvc.perform(post("/api/v1/project")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", jwt.jwt())
                .content("""
                         {
                                "projectName": "foobar",
                                "projectLeadId": notAnId,
                                "customerId": 789
                                }
                        """)
        );

        result
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.projectId").doesNotExist());
    }


}
