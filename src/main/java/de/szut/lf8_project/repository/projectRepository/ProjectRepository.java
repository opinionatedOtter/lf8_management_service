package de.szut.lf8_project.repository.projectRepository;

import de.szut.lf8_project.common.ValueType;
import de.szut.lf8_project.domain.customer.Customer;
import de.szut.lf8_project.domain.customer.CustomerId;
import de.szut.lf8_project.domain.project.*;
import de.szut.lf8_project.repository.RepositoryException;
import de.szut.lf8_project.repository.TeamMemberMapper;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ProjectRepository {

    private final ProjectDataRepository projectDataRepository;
    private final TeamMemberMapper teamMemberMapper;

    public ProjectRepository(ProjectDataRepository projectDataRepository,
                             TeamMemberMapper teamMemberMapper
    ) {
        this.projectDataRepository = projectDataRepository;
        this.teamMemberMapper = teamMemberMapper;
    }

    public Project saveProject(Project project) throws RepositoryException {
        return mapProjectDataToProject(projectDataRepository.save(ProjectData.builder()
                .projectId(project.getProjectId().map(ValueType::unbox).orElse(null))
                .projectName(project.getProjectName().unbox())
                .projectLeadId(project.getProjectLead().getProjectLeadId().unbox())
                .customerContactId(project.getCustomerContact().getCustomerContactId().unbox())
                .projectDescription(project.getProjectDescription().map(ValueType::unbox).orElse(null))
                .actualEndDate(project.getActualEndDate().map(ValueType::unbox).orElse(null))
                .plannedEndDate(project.getPlannedEndDate().map(ValueType::unbox).orElse(null))
                .actualEndDate(project.getActualEndDate().map(ValueType::unbox).orElse(null))
                .startDate(project.getStartDate().map(ValueType::unbox).orElse(null))
                .build()));
    }

    private Project mapProjectDataToProject(ProjectData projectData) {
        return Project.builder()
                .projectId(Optional.of(new ProjectId(projectData.projectId)))
                .projectDescription(Objects.isNull(projectData.projectDescription) ? Optional.empty() : Optional.of(new ProjectDescription(projectData.projectDescription)))
                .projectName(new ProjectName(projectData.projectName))
                .startDate(Objects.isNull(projectData.startDate) ? Optional.empty() : Optional.of(new StartDate(projectData.startDate)))
                .plannedEndDate(Objects.isNull(projectData.plannedEndDate) ? Optional.empty() : Optional.of(new PlannedEndDate(projectData.plannedEndDate)))
                .actualEndDate(Objects.isNull(projectData.actualEndDate) ? Optional.empty() : Optional.of(new ActualEndDate(projectData.actualEndDate)))
                .customer(new Customer(new CustomerId(projectData.customerId)))
                .projectLead(new ProjectLead(new ProjectLeadId(projectData.projectLeadId)))
                .customerContact(new CustomerContact(new CustomerContactId(projectData.customerContactId)))
                .teamMembers(projectData.teamMembers.stream().map(teamMemberMapper::mapTo).collect(Collectors.toSet()))
                .build();

    }
}
