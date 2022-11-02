package de.szut.lf8_project.repository.projectRepository;

import de.szut.lf8_project.domain.customer.Customer;
import de.szut.lf8_project.domain.customer.CustomerId;
import de.szut.lf8_project.domain.project.*;
import de.szut.lf8_project.repository.RepositoryException;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ProjectRepository {

    private ProjectDataRepository projectDataRepository;

    public ProjectRepository(ProjectDataRepository projectDataRepository) {
        this.projectDataRepository = projectDataRepository;
    }

    public Project createProject(Project project) throws RepositoryException {
        throw new NotYetImplementedException();
    }

    private Project mapProjectDataToProject(ProjectData projectData) {
        return Project.builder()
                .projectId(Optional.of(new ProjectId(projectData.projectId)))
                .projectDescription(new ProjectDescription(projectData.projectDescription))
                .projectName(new ProjectName(projectData.projectName))
                .startDate(new StartDate(projectData.startDate))
                .plannedEndDate(new PlannedEndDate(projectData.plannedEndDate))
                .actualEndDate(Optional.of(new ActualEndDate(projectData.actualEndDate)))
                .customer(new Customer(new CustomerId(1L)))
                .projectLead(new ProjectLead(new ProjectLeadId(projectData.responsibleEmployee)))
                // TODO MS-2
                //.teamMembers(projectData.teamMembers);
                .build();

    }
}
