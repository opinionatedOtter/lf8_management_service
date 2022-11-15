package de.szut.lf8_project.application;

import de.szut.lf8_project.common.ErrorDetail;
import de.szut.lf8_project.common.Errorcode;
import de.szut.lf8_project.common.FailureMessage;
import de.szut.lf8_project.controller.dtos.EmployeesOfProjectView;
import de.szut.lf8_project.domain.project.Project;
import de.szut.lf8_project.domain.project.ProjectId;
import de.szut.lf8_project.repository.RepositoryException;
import de.szut.lf8_project.repository.projectRepository.ProjectRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeApplicationService {
    private final ProjectRepository projectRepository;

    public EmployeeApplicationService(final ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public EmployeesOfProjectView getEmployeesOfProject(final ProjectId projectId) {
        try {
            return mapToEmployeeView(projectRepository.getProject(projectId));
        } catch (RepositoryException e) {
            throw new ApplicationServiceException(e.getErrorDetail());
        }
    }

    private EmployeesOfProjectView mapToEmployeeView(Project project) {
        return EmployeesOfProjectView.builder()
                .projectId(project.getProjectId().orElseThrow(
                                () -> new ApplicationServiceException(
                                new ErrorDetail(
                                        Errorcode.UNEXPECTED_ERROR,
                                        new FailureMessage("Project should have had an ID")
                                )
                        )
                ))
                .projectName(project.getProjectName())
                .teamMember(project.getTeamMembers())
                .build();
    }
}
