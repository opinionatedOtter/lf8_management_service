package de.szut.lf8_project.application;

import de.szut.lf8_project.common.*;
import de.szut.lf8_project.controller.dtos.AddEmployeeCommand;
import de.szut.lf8_project.controller.dtos.CreateProjectCommand;
import de.szut.lf8_project.controller.dtos.ProjectView;
import de.szut.lf8_project.controller.dtos.UpdateProjectCommand;
import de.szut.lf8_project.domain.DateService;
import de.szut.lf8_project.domain.adapter.EmployeeRepository;
import de.szut.lf8_project.domain.customer.Customer;
import de.szut.lf8_project.domain.customer.CustomerId;
import de.szut.lf8_project.domain.customer.CustomerService;
import de.szut.lf8_project.domain.employee.Employee;
import de.szut.lf8_project.domain.employee.EmployeeId;
import de.szut.lf8_project.domain.project.*;
import de.szut.lf8_project.repository.RepositoryException;
import de.szut.lf8_project.repository.projectRepository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class ProjectApplicationService {

    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final DateService dateService;
    private final CustomerService customerService;
    private final ProjectService projectService;

    public ProjectApplicationService(EmployeeRepository employeeRepository, ProjectRepository projectRepository, DateService dateService, CustomerService customerService, ProjectService projectService) {
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
        this.dateService = dateService;
        this.customerService = customerService;
        this.projectService = projectService;
    }

    public ProjectView createProject(CreateProjectCommand cmd, JWT jwt) throws ApplicationServiceException {
        validateProjectStartAndEnd(cmd.getStartDate(), cmd.getPlannedEndDate());
        validateCustomer(cmd.getCustomerId());

        ProjectLead projectLead = getProjectLead(cmd.getProjectLeadId(), jwt);

        return mapProjectToViewModel(saveProject(Project.builder()
                .projectId(Optional.empty())
                .projectLead(new ProjectLead(projectLead.getProjectLeadId()))
                .projectName(cmd.getProjectName())
                .customer(new Customer(cmd.getCustomerId()))
                .projectDescription(cmd.getProjectDescription())
                .actualEndDate(Optional.empty())
                .plannedEndDate(cmd.getPlannedEndDate())
                .startDate(cmd.getStartDate())
                .customerContact(cmd.getCustomerContact())
                .build()
        ));
    }

    public ProjectView addEmployee(AddEmployeeCommand cmd, ProjectId projectId, JWT jwt) {
        Project protectToUpdate = getProject(projectId);

        Employee employee = getEmployee(cmd.getEmployeeId(), jwt);

        Project newProject = addProjectMember(cmd, protectToUpdate, employee);

        return mapProjectToViewModel(saveProject(newProject));
    }

    private Project addProjectMember(AddEmployeeCommand cmd, Project project, Employee employee) {
        try {
            return projectService.addEmployeeToProject(cmd.getProjectRoles(), project, employee);
        } catch (ServiceException e) {
            throw new ApplicationServiceException(e.getErrorDetail());
        }
    }

    public ProjectView updateProject(UpdateProjectCommand cmd, ProjectId projectId, JWT jwt) {
        Project projectToUpdate = getProject(projectId);
        validateDates(cmd, projectToUpdate);
        cmd.getCustomerId().ifPresent(this::validateCustomer);
        ProjectLead projectLead = cmd.getProjectLeadId().map(id -> getProjectLead(id, jwt)).orElse(projectToUpdate.getProjectLead());
        // remove with time conflicts optional force flag?

        return mapProjectToViewModel(saveProject(Project.builder()
                .projectId(projectToUpdate.getProjectId())
                .projectLead(projectLead)
                .projectName(cmd.getProjectName().orElse(projectToUpdate.getProjectName()))
                .customerContact(cmd.getCustomerContact().orElse(projectToUpdate.getCustomerContact()))
                .customer(cmd.getCustomerId().map(Customer::new).orElse(projectToUpdate.getCustomer()))
                .projectDescription(cmd.getProjectDescription().isPresent() ? cmd.getProjectDescription() : projectToUpdate.getProjectDescription())
                .actualEndDate(cmd.getActualEndDate().isPresent() ? cmd.getActualEndDate() : projectToUpdate.getActualEndDate())
                .plannedEndDate(cmd.getPlannedEndDate().isPresent() ? cmd.getPlannedEndDate() : projectToUpdate.getPlannedEndDate())
                .startDate(cmd.getStartDate().isPresent() ? cmd.getStartDate() : projectToUpdate.getStartDate())
                .build()
        ));
    }

    private void validateDates(UpdateProjectCommand cmd, Project projectToUpdate) {
        Optional<StartDate> start = cmd.getStartDate().isPresent() ? cmd.getStartDate() : projectToUpdate.getStartDate();
        Optional<PlannedEndDate> plannedEnd = cmd.getPlannedEndDate().isPresent() ? cmd.getPlannedEndDate() : projectToUpdate.getPlannedEndDate();
        Optional<ActualEndDate> actualEnd = cmd.getActualEndDate().isPresent() ? cmd.getActualEndDate() : projectToUpdate.getActualEndDate();
        try {
            if (start.isPresent()) {
                if (plannedEnd.isPresent()) {
                    dateService.validateProjectStartAndPlannedEnd(start.get(), plannedEnd.get());
                }
                if (actualEnd.isPresent()) {
                    dateService.validateProjectStartAndActualEnd(start.get(), actualEnd.get());
                }
            }
        } catch (ServiceException e) {
            throw new ApplicationServiceException(e.getErrorDetail());
        }
    }

    private Project getProject(ProjectId projectId) {
        try {
            return projectRepository.getProjectById(projectId);
        } catch (RepositoryException e) {
            throw new ApplicationServiceException(e.getErrorDetail());
        }
    }

    private void validateCustomer(CustomerId customerId) {
        try {
            customerService.validateCustomer(customerId);
        } catch (ServiceException e) {
            throw new ApplicationServiceException(e.getErrorDetail());
        }
    }

    private Project saveProject(Project project) {
        try {
            return projectRepository.saveProject(project);
        } catch (RepositoryException e) {
            throw new ApplicationServiceException(e.getErrorDetail());
        }
    }

    private Employee getEmployee(EmployeeId employeeId, JWT jwt) {
        try {
            return employeeRepository.getEmployeeById(jwt, employeeId);
        } catch (RepositoryException e) {
            throw new ApplicationServiceException(e.getErrorDetail());
        }
    }

    private ProjectLead getProjectLead(ProjectLeadId projectLeadId, JWT jwt) {
        return new ProjectLead(new ProjectLeadId(getEmployee(projectLeadId, jwt).getId().unbox()));
    }

    private void validateProjectStartAndEnd(Optional<StartDate> start, Optional<PlannedEndDate> end) {
        if (start.isPresent() && end.isPresent()) {
            try {
                dateService.validateProjectStartAndPlannedEnd(
                        start.get(),
                        end.get());
            } catch (ServiceException e) {
                throw new ApplicationServiceException(e.getErrorDetail());
            }
        }
    }

    private ProjectView mapProjectToViewModel(Project project) {
        return ProjectView.builder()
                .projectId(project.getProjectId().orElseThrow(() -> new ApplicationServiceException(new ErrorDetail(Errorcode.UNEXPECTED_ERROR, new FailureMessage("Oops, something went wrong.")))))
                .projectLead(project.getProjectLead())
                .projectDescription(project.getProjectDescription())
                .projectName(project.getProjectName())
                .startDate(project.getStartDate())
                .actualEndDate(project.getActualEndDate())
                .plannedEndDate(project.getPlannedEndDate())
                .customer(project.getCustomer())
                .customerContact(project.getCustomerContact())
                .teamMember(project.getTeamMembers())
                .build();
    }
}
