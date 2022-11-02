package de.szut.lf8_project.application;

import de.szut.lf8_project.common.JWT;
import de.szut.lf8_project.common.ServiceException;
import de.szut.lf8_project.controller.dtos.CreateProjectDto;
import de.szut.lf8_project.controller.dtos.ProjectView;
import de.szut.lf8_project.domain.DateService;
import de.szut.lf8_project.domain.adapter.EmployeeRepository;
import de.szut.lf8_project.domain.employee.Employee;
import de.szut.lf8_project.domain.employee.EmployeeId;
import de.szut.lf8_project.domain.project.Project;
import de.szut.lf8_project.domain.project.ProjectService;
import de.szut.lf8_project.repository.RepositoryException;
import de.szut.lf8_project.repository.projectRepository.ProjectRepository;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.stereotype.Service;


@Service
public class ProjectApplicationService {

    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final DateService dateService;
    private final ProjectService projectService;

    public ProjectApplicationService(EmployeeRepository employeeRepository, ProjectRepository projectRepository, DateService dateService, ProjectService projectService) {
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
        this.dateService = dateService;
        this.projectService = projectService;
    }

    public ProjectView createProject(CreateProjectDto createProjectDto, JWT jwt) throws ApplicationServiceException {
        //TODO ApplicationServicexception ausformulieren - Runtime? Catch in controller to Json etc.
        Project pendingProject = Project.fromDto(createProjectDto);
        validateProjectStartAndEnd(pendingProject);
        // TODO: check if customer exists

        // loop here later
        //TODO: discuss: typisierung und wann dto zu project - was ist mit optionals etc?
        validateTeamMemberForProject(new EmployeeId(pendingProject.getProjectLead().getProjectLeadId().unbox()), pendingProject, jwt);

        return mapProjectToViewModel(saveNewProject(pendingProject));


        // Datum valide? -> Domainservice validate -> Either

        // gibt es den Kunden? -> KundenDummyRepo - Optional oder Statuscodeexception
        // check if everyone exist -> call api - Optional oder Statuscodeexception

        // get (all) Mitarbeiter from Mitarbeiterservice

        // LATER: Is everyone free in this timewindow? projectservice.validate -> either
        //LATER: are qualifications fine?
    }

    private Project saveNewProject(Project project) {
        try {
            return projectRepository.createProject(project);
        } catch (RepositoryException e) {
            throw new ApplicationServiceException(e.getErrorDetail());
        }
    }

    private void validateTeamMemberForProject(EmployeeId employeeId, Project project, JWT jwt) {
        try {
            Employee employee = employeeRepository.getEmployeeById(jwt, employeeId);
            projectService.checkIsValidTeamMember(project, employee);
        } catch (RepositoryException | ServiceException e) {
            throw new ApplicationServiceException(e.getErrorDetail());
        }
    }

    private void validateProjectStartAndEnd(Project project) {
        try {
            dateService.validateProjectStartAndEnd(
                    project.getStartDate(),
                    project.getPlannedEndDate());
        } catch (ServiceException e) {
            throw new ApplicationServiceException(e.getErrorDetail());
        }
    }

    private ProjectView mapProjectToViewModel(Project project) {
        throw new NotYetImplementedException();
    }
}
