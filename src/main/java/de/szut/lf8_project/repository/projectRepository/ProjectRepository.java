package de.szut.lf8_project.repository.projectRepository;

import de.szut.lf8_project.domain.customer.Customer;
import de.szut.lf8_project.domain.customer.CustomerId;
import de.szut.lf8_project.domain.project.*;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectRepository {

    private ProjectDataRepository projectDataRepository;

    public ProjectRepository(ProjectDataRepository projectDataRepository) {
        this.projectDataRepository = projectDataRepository;
    }

    private Project mapProjectDataToProject(ProjectData projectData) {
        return Project.builder()
                .projectId(new ProjectId(projectData.projectId))
                .projectDescription(new ProjectDescription(projectData.projectDescription))
                .projectName(new ProjectName(projectData.projectName))
                .startDate(new StartDate(projectData.startDate))
                .plannedEndDate(new PlannedEndDate(projectData.plannedEndDate))
                .actualEndDate(new ActualEndDate(projectData.actualEndDate))
                .customer(new Customer(new CustomerId(1L)))
                .projectLead(new ProjectLead(new ProjectLeadId(projectData.responsibleEmployee)))
                .build();
                //.teamMembers(projectData.teamMembers);
    }
}
