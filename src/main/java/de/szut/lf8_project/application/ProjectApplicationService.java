package de.szut.lf8_project.application;

import de.szut.lf8_project.common.JWT;
import de.szut.lf8_project.common.ServiceException;
import de.szut.lf8_project.controller.dtos.CreateProjectCommand;
import de.szut.lf8_project.controller.dtos.ProjectView;
import de.szut.lf8_project.domain.CustomerService;
import de.szut.lf8_project.domain.CustomerServiceException;
import de.szut.lf8_project.domain.DateService;
import de.szut.lf8_project.domain.adapter.EmployeeRepository;
import de.szut.lf8_project.domain.customer.Customer;
import de.szut.lf8_project.domain.customer.CustomerId;
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
        validateProjectStartAndEnd(cmd.startDate(), cmd.plannedEndDate());
        validateCustomer(cmd.customerId());

        Employee projectLead = getEmployee(cmd.projectLead(), jwt);

        return mapProjectToViewModel(saveNewProject(Project.builder()
                .projectId(Optional.empty())
                .projectLead(new ProjectLead(new ProjectLeadId(projectLead.getId().unbox())))
                .projectName(cmd.projectName())
                .customer(new Customer(cmd.customerId()))
                .projectDescription(cmd.projectDescription())
                .actualEndDate(Optional.empty())
                .plannedEndDate(cmd.plannedEndDate())
                .startDate(cmd.startDate())
                .customerContact(new CustomerContact(cmd.contactPersonId()))
                .build()
        ));


        // get (all) Mitarbeiter from Mitarbeiterservice

        // LATER: Is everyone free in this timewindow? projectservice.validate -> either
        //LATER: are qualifications fine?
    }

    private void validateCustomer(CustomerId customerId) {
        try {
            customerService.validateCustomer(customerId);
        } catch (CustomerServiceException e) {
            throw new ApplicationServiceException(e.getErrorDetail());
        }
    }

    private Project saveNewProject(Project project) {
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

    private void validateProjectStartAndEnd(Optional<StartDate> start, Optional<PlannedEndDate> end) {
        if (start.isPresent() && end.isPresent()) {
            try {
                dateService.validateProjectStartAndEnd(
                        start.get(),
                        end.get());
            } catch (ServiceException e) {
                throw new ApplicationServiceException(e.getErrorDetail());
            }
        }
    }

    private ProjectView mapProjectToViewModel(Project project) {
        return ProjectView.builder()
                // TODO - Optionals + Jackson?
                .projectId(project.getProjectId().orElse(null))
                .projectLead(project.getProjectLead())
                .projectDescription(project.getProjectDescription().orElse(null))
                .projectName(project.getProjectName())
                .startDate(project.getStartDate().orElse(null))
                .actualEndDate(project.getActualEndDate().orElse(null))
                .plannedEndDate(project.getPlannedEndDate().orElse(null))
                .customer(project.getCustomer())
                .teamMember(project.getTeamMembers())
                .build();
    }
}
