package de.szut.lf8_project.application;

import de.szut.lf8_project.common.*;
import de.szut.lf8_project.controller.dtos.CreateProjectCommand;
import de.szut.lf8_project.controller.dtos.ProjectView;
import de.szut.lf8_project.domain.CustomerService;
import de.szut.lf8_project.domain.DateService;
import de.szut.lf8_project.domain.adapter.EmployeeRepository;
import de.szut.lf8_project.domain.customer.Customer;
import de.szut.lf8_project.domain.customer.CustomerId;
import de.szut.lf8_project.domain.employee.*;
import de.szut.lf8_project.domain.project.*;
import de.szut.lf8_project.repository.RepositoryException;
import de.szut.lf8_project.repository.projectRepository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("The ProjectApplicationService should")
public class TestProjectApplicationService {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private DateService dateService;
    @Mock
    private CustomerService customerService;
    @Mock
    private ProjectService projectService;
    @InjectMocks
    private ProjectApplicationService projectApplicationService;

    private final ProjectId projectId = new ProjectId(1l);
    private final ProjectName projectName = new ProjectName("Projekty");
    private final ProjectLead projectLead = new ProjectLead(new ProjectLeadId(2L));
    private final Customer customer = new Customer(new CustomerId(1L));
    private final CustomerContact customerContact = new CustomerContact("Kontakty");
    private final ProjectDescription projectDescription = new ProjectDescription("Beschreiby");
    private final StartDate startDate = new StartDate("2002-01-11");
    private final PlannedEndDate plannedEndDate = new PlannedEndDate("2002-03-12");
    private final JWT jwt = new JWT("Toky");
    private final ErrorDetail errorDetail = new ErrorDetail(Errorcode.UNEXPECTED_ERROR, new FailureMessage("Faily"));

    @Test
    @DisplayName("create a project")
    public void createProject() throws ServiceException, RepositoryException {
        ProjectView expectedView = aDefaultView().build();
        CreateProjectCommand cmd = aDefaultCreateCommand().build();
        when(employeeRepository.getEmployeeById(jwt, cmd.getProjectLeadId())).thenReturn(aDefaultEmployee().id(cmd.getProjectLeadId()).build());
        when(projectRepository.saveProject(any())).thenReturn(aDefaultProject().build());


        ProjectView result = projectApplicationService.createProject(cmd, jwt);

        assertThat(result).isEqualTo(expectedView);
        verify(dateService, times(1)).validateProjectStartAndEnd(cmd.getStartDate().get(), cmd.getPlannedEndDate().get());
        verify(customerService, times(1)).validateCustomer(cmd.getCustomerId());
    }

    @Test
    @DisplayName("handle employeeRepository exception")
    public void handleEmployeeRepo() throws RepositoryException {
        CreateProjectCommand cmd = aDefaultCreateCommand().build();
        when(employeeRepository.getEmployeeById(jwt, cmd.getProjectLeadId())).thenThrow(new RepositoryException(errorDetail));

        ApplicationServiceException result = assertThrows(ApplicationServiceException.class, () -> projectApplicationService.createProject(cmd, jwt));

        assertThat(result.getErrorDetail()).isEqualTo(errorDetail);
        verify(employeeRepository, times(1)).getEmployeeById(jwt, cmd.getProjectLeadId());
        verifyNoInteractions(projectRepository);
    }

    @Test
    @DisplayName("handle projectRepository exception")
    public void handleProjectRepo() throws RepositoryException {
        CreateProjectCommand cmd = aDefaultCreateCommand().build();
        when(employeeRepository.getEmployeeById(jwt, cmd.getProjectLeadId())).thenReturn(aDefaultEmployee().id(cmd.getProjectLeadId()).build());
        when(projectRepository.saveProject(any())).thenThrow(new RepositoryException(errorDetail));

        ApplicationServiceException result = assertThrows(ApplicationServiceException.class, () -> projectApplicationService.createProject(cmd, jwt));

        assertThat(result.getErrorDetail()).isEqualTo(errorDetail);
        verify(projectRepository, times(1)).saveProject(any());
        verifyNoMoreInteractions(projectRepository);
    }

    @Test
    @DisplayName("handle datesService exception")
    public void handleDateService() throws ServiceException {
        CreateProjectCommand cmd = aDefaultCreateCommand().build();
        doThrow(new ServiceException(errorDetail)).when(dateService).validateProjectStartAndEnd(startDate, plannedEndDate);

        ApplicationServiceException result = assertThrows(ApplicationServiceException.class, () -> projectApplicationService.createProject(cmd, jwt));

        assertThat(result.getErrorDetail()).isEqualTo(errorDetail);
        verify(dateService, times(1)).validateProjectStartAndEnd(startDate, plannedEndDate);
        verifyNoInteractions(projectRepository);
    }

    @Test
    @DisplayName("handle customerService exception")
    public void handleCustomerService() throws ServiceException {
        CreateProjectCommand cmd = aDefaultCreateCommand().build();
        doThrow(new ServiceException(errorDetail)).when(customerService).validateCustomer(customer.getCustomerId());

        ApplicationServiceException result = assertThrows(ApplicationServiceException.class, () -> projectApplicationService.createProject(cmd, jwt));

        assertThat(result.getErrorDetail()).isEqualTo(errorDetail);
        verify(customerService, times(1)).validateCustomer(customer.getCustomerId());
        verifyNoInteractions(projectRepository);
    }

    private Employee.EmployeeBuilder aDefaultEmployee() {
        return Employee.builder()
                .id(new EmployeeId(1L))
                .firstName(new FirstName("Namy"))
                .lastName(new LastName("Lasty"))
                .postcode(new Postcode("28983"))
                .skillset(List.of(new Qualification("Skillz")))
                .street(new Street("Streety"));
    }

    private CreateProjectCommand.CreateProjectCommandBuilder aDefaultCreateCommand() {
        return CreateProjectCommand.builder()
                .projectName(projectName)
                .projectLeadId(projectLead.getProjectLeadId())
                .customerContact(customerContact)
                .customerId(customer.getCustomerId())
                .startDate(Optional.of(startDate))
                .plannedEndDate(Optional.of(plannedEndDate))
                .projectDescription(Optional.of(projectDescription));
    }

    private ProjectView.ProjectViewBuilder aDefaultView() {
        return ProjectView.builder()
                .projectName(projectName)
                .projectLead(projectLead)
                .customerContact(customerContact)
                .customer(customer)
                .startDate(Optional.of(startDate))
                .plannedEndDate(Optional.of(plannedEndDate))
                .projectId(projectId)
                .actualEndDate(Optional.empty())
                .teamMember(Collections.emptySet())
                .projectDescription(Optional.of(projectDescription));
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
}
