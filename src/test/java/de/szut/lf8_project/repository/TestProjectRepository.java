package de.szut.lf8_project.repository;

import de.szut.lf8_project.WithAppContextContainerTest;
import de.szut.lf8_project.common.ErrorDetail;
import de.szut.lf8_project.common.Errorcode;
import de.szut.lf8_project.common.FailureMessage;
import de.szut.lf8_project.domain.customer.Customer;
import de.szut.lf8_project.domain.customer.CustomerId;
import de.szut.lf8_project.domain.project.*;
import de.szut.lf8_project.repository.projectRepository.ProjectDataRepository;
import de.szut.lf8_project.repository.projectRepository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@DisplayName("The EmployeeRestRepository should")
public class TestProjectRepository extends WithAppContextContainerTest {

    private final ProjectId projectId = new ProjectId(1L);
    private final ProjectName projectName = new ProjectName("Projekty");
    private final ProjectLead projectLead = new ProjectLead(new ProjectLeadId(2L));
    private final Customer customer = new Customer(new CustomerId(1L));
    private final CustomerContact customerContact = new CustomerContact("Kontakty");
    private final ProjectDescription projectDescription = new ProjectDescription("Beschreiby");
    private final StartDate startDate = new StartDate("2002-01-11");
    private final PlannedEndDate plannedEndDate = new PlannedEndDate("2002-03-12");

    @Autowired
    ProjectRepository projectRepository;

    @Test
    @DisplayName("save a project")
    public void saveProject() throws RepositoryException {
        Project given = aDefaultProject().build();

        Project result = projectRepository.saveProject(given);

        assertThat(result).isEqualTo(given);
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("teamMembers")
                .isEqualTo(getProjectFromDb(given.getProjectId().get()));
    }

    @Test
    @DisplayName("throw an exception if the project could not be saved")
    public void saveProjectException() {
        Project project = aDefaultProject().build();
        ErrorDetail expected = new ErrorDetail(Errorcode.UNEXPECTED_ERROR, new FailureMessage("An unknown error occurred"));
        ProjectDataRepository mockRepo = Mockito.mock(ProjectDataRepository.class);
        when(mockRepo.save(any())).thenThrow(new OptimisticLockingFailureException("whoops"));
        ProjectRepository projectRepositoryWithMock = new ProjectRepository(mockRepo, new TeamMemberMapper());

        RepositoryException result = assertThrows(RepositoryException.class, () -> projectRepositoryWithMock.saveProject(project));

        assertThat(result.getErrorDetail()).isEqualTo(expected);
    }


    private Project.ProjectBuilder aDefaultProject() {
        return Project.builder()
                .projectId(Optional.of(projectId))
                .projectName(projectName)
                .projectLead(projectLead)
                .customerContact(customerContact)
                .customer(customer)
                .startDate(Optional.of(startDate))
                .plannedEndDate(Optional.of(plannedEndDate))
                .actualEndDate(Optional.empty())
                .projectDescription(Optional.of(projectDescription))
                .teamMembers(Collections.emptySet());
    }

    private Project getProjectFromDb(ProjectId projectId) {
        return jdbcTemplate.queryForObject("SELECT * FROM project WHERE project_id=?",
                (rs, rowNum) -> Project.builder()
                        .projectId(Optional.of(new ProjectId(rs.getLong("project_id"))))
                        .projectName(new ProjectName(rs.getString("project_name")))
                        .projectLead(new ProjectLead(new ProjectLeadId(rs.getLong("project_lead_id"))))
                        .customerContact(new CustomerContact(rs.getString("customer_contact")))
                        .customer(new Customer(new CustomerId(rs.getLong("customer_id"))))
                        .startDate(rs.getDate("start_date") == null ? Optional.empty() : Optional.of(new StartDate(rs.getDate("start_date").toLocalDate())))
                        .plannedEndDate(rs.getDate("planned_end_date") == null ? Optional.empty() : Optional.of(new PlannedEndDate(rs.getDate("planned_end_date").toLocalDate())))
                        .actualEndDate(rs.getDate("actual_end_date") == null ? Optional.empty() : Optional.of(new ActualEndDate(rs.getDate("actual_end_date").toLocalDate())))
                        .projectDescription(rs.getString("project_description") == null ? Optional.empty() : Optional.of(new ProjectDescription(rs.getString("project_description"))))
                        .teamMembers(Collections.emptySet())
                        .build(),
                projectId.unbox());
    }
}
