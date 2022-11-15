package de.szut.lf8_project.integration;

import de.szut.lf8_project.FullIntegrationTest;
import de.szut.lf8_project.domain.employee.Employee;
import de.szut.lf8_project.domain.employee.ProjectRole;
import de.szut.lf8_project.domain.project.Project;
import de.szut.lf8_project.domain.project.ProjectId;
import de.szut.lf8_project.domain.project.ProjectName;
import de.szut.lf8_project.domain.project.TeamMember;
import de.szut.lf8_project.repository.RepositoryException;
import de.szut.lf8_project.repository.projectRepository.ProjectData;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Die DELETE Project by ID Rest-Methode")
public class TestDeleteProject extends FullIntegrationTest {

    @Test
    @DisplayName("sollte erfolgreich ein simples Projekt löschen")
    void deleteSimpleProject() throws Exception {
        Project project = createAndSaveDefaultProjectWithProjectLead();
        ProjectData projectFromDb = getProjectFromDb(project.getProjectId().get());

        ResultActions result = mockMvc.perform(delete("/api/v1/project/" + project.getProjectId().get().unbox())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", jwt.jwt())
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());


        assertThat(projectFromDb.getProjectId().equals(project.getProjectId().get().unbox()));
        assertThrows(RepositoryException.class, () -> getProjectByIdFromDatabase(project.getProjectId().get()));
        assertThrows(EmptyResultDataAccessException.class, () -> getProjectFromDb(project.getProjectId().get()));
    }

    @Test
    @DisplayName("sollte erfolgreich ein komplexes Projekt löschen")
    void deleteProjectWithTeamMembers() throws Exception {
        Project project = createAndSaveDefaultProjectWithProjectLead();

        ProjectRole projectRole = createAndSaveQualificationInRemoteRepository();
        Employee employee = saveEmployeeInRemoteRepository(createDefaultEmployeeWithRolesWith0Id(List.of(projectRole)));
        project.getTeamMembers().add(new TeamMember(employee.getId(), projectRole));
        Project finalProject = saveProjectInDatabase(project);
        ProjectData projectFromDb = getProjectFromDb(project.getProjectId().get());

        ResultActions result = mockMvc.perform(delete("/api/v1/project/" + project.getProjectId().get().unbox())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", jwt.jwt())
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        assertThat(projectFromDb.getProjectId().equals(project.getProjectId().get().unbox()));
        assertThrows(RepositoryException.class, () -> getProjectByIdFromDatabase(finalProject.getProjectId().get()));
        assertThrows(EmptyResultDataAccessException.class, () -> getProjectFromDb(project.getProjectId().get()));
        assertThat(countTeamMembersOfProject(project.getProjectId().get())).isEqualTo(0);
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

    private ProjectData getProjectFromDb(ProjectId projectId) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM project WHERE project_id = ?",
                getProjectDataRowMapper(),
                projectId.unbox()
        );
    }

    private RowMapper<ProjectData> getProjectDataRowMapper() {
        return (rs, rowNum) -> ProjectData.builder()
                .projectId(rs.getLong("project_id"))
                .projectName(rs.getString("project_name"))
                .projectDescription(rs.getString("project_description"))
                .customerContact(rs.getString("customer_contact"))
                .customerId(rs.getLong("customer_id"))
                .projectLeadId(rs.getLong("project_lead_id"))
                .actualEndDate(rs.getDate("actual_end_date").toLocalDate())
                .plannedEndDate(rs.getDate("planned_end_date").toLocalDate())
                .startDate(rs.getDate("start_date").toLocalDate())
                .build();
    }

    private int countTeamMembersOfProject(ProjectId projectId) {
        return jdbcTemplate.queryForObject(
                "SELECT count(*) as counter from team_member where project_id = ?",
                (rs, i)  -> rs.getInt("counter"),
                projectId.unbox()
        );
    }
}