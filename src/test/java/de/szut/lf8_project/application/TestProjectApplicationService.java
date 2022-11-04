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
import de.szut.lf8_project.domain.employee.*;
import de.szut.lf8_project.domain.project.*;
import de.szut.lf8_project.repository.RepositoryException;
import de.szut.lf8_project.repository.projectRepository.ProjectRepository;
import lombok.Builder;
import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
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
    private final  JWT jwt = new JWT("Toky");


    @Test
    @DisplayName("create a project")
    public void createProject() throws ServiceException, CustomerServiceException, RepositoryException {
        CreateProjectCommand cmd = aDefaultCreateCommand().build();
        when(employeeRepository.getEmployeeById(jwt, cmd.getProjectLeadId())).thenReturn(aDefaultEmployee().id(cmd.getProjectLeadId()).build());
        when(projectRepository.saveProject(any())).thenReturn(aDefaultProject().build());


        ProjectView result = projectApplicationService.createProject(cmd, jwt);

        assertThat(result.getProjectName()).isEqualTo(cmd.getProjectName());
        assertThat(result.getProjectLead().getProjectLeadId()).isEqualTo(cmd.getProjectLeadId());
        assertThat(result.getCustomerContact()).isEqualTo(cmd.getCustomerContact());
        assertThat(result.getCustomer().getCustomerId()).isEqualTo(cmd.getCustomerId());
        assertThat(result.getStartDate()).isEqualTo(cmd.getStartDate());
        assertThat(result.getPlannedEndDate()).isEqualTo(cmd.getPlannedEndDate());
        assertThat(result.getProjectDescription()).isEqualTo(cmd.getProjectDescription());
        verify(dateService, times(1)).validateProjectStartAndEnd(cmd.getStartDate().get(), cmd.getPlannedEndDate().get());
        verify(customerService, times(1)).validateCustomer(cmd.getCustomerId());
    }

    private Employee.EmployeeBuilder aDefaultEmployee(){
        return Employee.builder()
                .id(new EmployeeId(1L))
                .firstName(new FirstName("Namy"))
                .lastName(new LastName("Lasty"))
                .postcode(new Postcode("28983"))
                .skillset(List.of(new Qualification("Skillz")))
                .street(new Street("Streety"));
    }

    private CreateProjectCommand.CreateProjectCommandBuilder aDefaultCreateCommand(){
        return CreateProjectCommand.builder()
                .projectName(projectName)
                .projectLeadId(projectLead.getProjectLeadId())
                .customerContact(customerContact)
                .customerId(customer.getCustomerId())
                .startDate(Optional.of(startDate))
                .plannedEndDate(Optional.of(plannedEndDate))
                .projectDescription(Optional.of(projectDescription));
    }

    private Project.ProjectBuilder aDefaultProject(){
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
