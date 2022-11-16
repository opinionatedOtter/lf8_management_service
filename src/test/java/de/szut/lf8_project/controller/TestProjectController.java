package de.szut.lf8_project.controller;

import de.szut.lf8_project.WithAppContextContainerTest;
import de.szut.lf8_project.application.ApplicationServiceException;
import de.szut.lf8_project.application.ProjectApplicationService;
import de.szut.lf8_project.common.ErrorDetail;
import de.szut.lf8_project.common.Errorcode;
import de.szut.lf8_project.common.FailureMessage;
import de.szut.lf8_project.common.JWT;
import de.szut.lf8_project.controller.dtos.CreateProjectCommand;
import de.szut.lf8_project.controller.dtos.ProjectView;
import de.szut.lf8_project.controller.dtos.UpdateProjectCommand;
import de.szut.lf8_project.domain.customer.Customer;
import de.szut.lf8_project.domain.customer.CustomerId;
import de.szut.lf8_project.domain.project.*;
import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("A Project controller should")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class TestProjectController extends WithAppContextContainerTest {

    private final JWT dummyJwt = new JWT("Bearer fake.fake.fake");
    private final String projectName = "foobar";
    private final String customerContact = "Testkontakt";
    private final String projectDescription = "foobar at the beach";
    private final LocalDate startDate = LocalDate.of(2022, 9, 23);
    private final LocalDate plannedEndDate = LocalDate.of(2022, 11, 15);
    private final LocalDate actualEndDate = LocalDate.of(2023, 11, 15);
    private final Long projectleadId = 456L;
    private final Long customerId = 789L;
    private final Long projectId = 123L;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProjectApplicationService projectApplicationService;

    private ProjectView.ProjectViewBuilder aDefaultProjectView() {
        return ProjectView.builder()
                .projectId(new ProjectId(projectId))
                .projectDescription(Optional.of(new ProjectDescription(projectDescription)))
                .projectLead(new ProjectLead(new ProjectLeadId(projectleadId)))
                .projectName(new ProjectName(projectName))
                .customer(new Customer(new CustomerId(customerId)))
                .actualEndDate(Optional.empty())
                .plannedEndDate(Optional.of(new PlannedEndDate(plannedEndDate)))
                .startDate(Optional.of(new StartDate(startDate)))
                .customerContact(new CustomerContact(customerContact))
                .teamMember(Collections.emptySet());
    }

    @Nested
    @DisplayName("create a project and should")
    class TestCreateProject {

        @Test
        @DisplayName("accept valid json and return the created entity")
        public void acceptValidJsonHappyPath() throws Exception {
            CreateProjectCommand validProject = aDefaultCreateCommand().build();
            ProjectView projectView = aDefaultProjectView().build();
            when(projectApplicationService.createProject(validProject, dummyJwt)).thenReturn(projectView);

            ResultActions result = mockMvc.perform(post("/api/v1/project")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", dummyJwt.jwt())
                    .content(String.format("""
                            {
                            "projectName": "%s",
                            "projectLeadId": %d,
                            "customerId": %d,
                            "customerContact": "%s",
                            "projectDescription": "%s",
                            "startDate": "%s",
                            "plannedEndDate": "%s"
                            }
                            """, projectName, projectleadId, customerId, customerContact, projectDescription, startDate, plannedEndDate))
            );

            result.andExpect(content()
                    .json(String.format("""
                                {
                                "projectId": %d,
                                "projectName": "%s",
                                "projectDescription": "%s",
                                "startDate": "%s",
                                "plannedEndDate": "%s",
                                 "projectLead": {
                                    "projectLeadId": %d
                                },
                                "customer" : {
                                    "customerId": %d
                                    }
                                }
                            """, projectId, projectName, projectDescription, startDate, plannedEndDate, projectleadId, customerId))
            ).andExpect(status().isCreated());
        }

        @Test
        @DisplayName("respond with Problem Detail when an error occurs")
        public void errorResponse() throws Exception {
            CreateProjectCommand validProject = aDefaultCreateCommand().build();
            ErrorDetail errorDetail = new ErrorDetail(Errorcode.UNEXPECTED_ERROR, new FailureMessage("sucks for you lol"));
            when(projectApplicationService.createProject(validProject, dummyJwt)).thenThrow(new ApplicationServiceException(errorDetail));

            ResultActions result = mockMvc.perform(post("/api/v1/project")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", dummyJwt.jwt())
                    .content(String.format("""
                            {
                            "projectName": "%s",
                            "projectLeadId": %d,
                            "customerId": %d,
                            "customerContact": "%s",
                            "projectDescription": "%s",
                            "startDate": "%s",
                            "plannedEndDate": "%s"
                            }
                            """, projectName, projectleadId, customerId, customerContact, projectDescription, startDate, plannedEndDate))
            );

            result.andExpect(content()
                    .json(String.format("""
                            {
                            "title": "%s",
                            "detail": "%s",
                            "status": "%d"
                            }
                            """, errorDetail.getErrorCode(), errorDetail.getFailureMessage(), errorDetail.getErrorCode().getHttpRepresentation().value()))
            ).andExpect(status().is(errorDetail.getErrorCode().getHttpRepresentation().value()));
        }


        @NonNull
        private CreateProjectCommand.CreateProjectCommandBuilder aDefaultCreateCommand() {
            return CreateProjectCommand.builder()
                    .projectName(new ProjectName(projectName))
                    .projectLeadId(new ProjectLeadId(projectleadId))
                    .customerId(new CustomerId(customerId))
                    .customerContact(new CustomerContact(customerContact))
                    .projectDescription(Optional.of(new ProjectDescription(projectDescription)))
                    .startDate(Optional.of(new StartDate(startDate)))
                    .plannedEndDate(Optional.of(new PlannedEndDate(plannedEndDate)));
        }
    }

    @Nested
    @DisplayName("update a project and should")
    class TestUpdateProject {

        @Test
        @DisplayName("accept valid json and return the updated entity")
        public void updateProject() throws Exception {
            UpdateProjectCommand validUpdate = aDefaultUpdateCommand().build();
            ProjectView projectView = aDefaultProjectView().actualEndDate(Optional.of(new ActualEndDate(actualEndDate))).build();
            ProjectId projectToUpdateId = new ProjectId(projectId);
            when(projectApplicationService.updateProject(validUpdate, projectToUpdateId, dummyJwt, false)).thenReturn(projectView);

            ResultActions result = mockMvc.perform(put("/api/v1/project/" + projectToUpdateId.unbox())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", dummyJwt.jwt())
                    .content(String.format("""
                            {
                            "projectName": "%s",
                            "projectLeadId": %d,
                            "customerId": %d,
                            "customerContact": "%s",
                            "projectDescription": "%s",
                            "startDate": "%s",
                            "plannedEndDate": "%s",
                            "actualEndDate": "%s"
                            }
                            """, projectName, projectleadId, customerId, customerContact, projectDescription, startDate, plannedEndDate, actualEndDate))
            );

            result.andExpect(content()
                    .json(String.format("""
                                {
                                "projectId": %d,
                                "projectName": "%s",
                                "projectDescription": "%s",
                                "startDate": "%s",
                                "plannedEndDate": "%s",
                                "actualEndDate": "%s",
                                 "projectLead": {
                                    "projectLeadId": %d
                                },
                                "customer" : {
                                    "customerId": %d
                                    }
                                }
                            """, projectToUpdateId.unbox(), projectName, projectDescription, startDate, plannedEndDate, actualEndDate, projectleadId, customerId))
            ).andExpect(status().isOk());
        }

        @Test
        @DisplayName("respond with Problem Detail when an error occurs")
        public void errorResponse() throws Exception {
            UpdateProjectCommand validUpdate = aDefaultUpdateCommand().build();
            ProjectId projectToUpdateId = new ProjectId(projectId);
            ErrorDetail errorDetail = new ErrorDetail(Errorcode.UNEXPECTED_ERROR, new FailureMessage("sucks for you lol"));
            when(projectApplicationService.updateProject(validUpdate, projectToUpdateId, dummyJwt, false)).thenThrow(new ApplicationServiceException(errorDetail));

            ResultActions result = mockMvc.perform(put("/api/v1/project/" + projectToUpdateId.unbox())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", dummyJwt.jwt())
                    .content(String.format("""
                            {
                            "projectName": "%s",
                            "projectLeadId": %d,
                            "customerId": %d,
                            "customerContact": "%s",
                            "projectDescription": "%s",
                            "startDate": "%s",
                            "plannedEndDate": "%s",
                            "actualEndDate": "%s"
                            }
                            """, projectName, projectleadId, customerId, customerContact, projectDescription, startDate, plannedEndDate, actualEndDate))
            );

            result.andExpect(content()
                    .json(String.format("""
                            {
                            "title": "%s",
                            "detail": "%s",
                            "status": "%d"
                            }
                            """, errorDetail.getErrorCode(), errorDetail.getFailureMessage(), errorDetail.getErrorCode().getHttpRepresentation().value()))
            ).andExpect(status().is(errorDetail.getErrorCode().getHttpRepresentation().value()));
        }

        @Test
        @DisplayName("properly recognize false force flag")
        public void forceFlagFalse() throws Exception {
            UpdateProjectCommand validUpdate = aDefaultUpdateCommand().build();
            ProjectId projectToUpdateId = new ProjectId(projectId);

            mockMvc.perform(put("/api/v1/project/" + projectToUpdateId.unbox() + "/?isForced=false")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", dummyJwt.jwt())
                    .content(String.format("""
                            {
                            "projectName": "%s",
                            "projectLeadId": %d,
                            "customerId": %d,
                            "customerContact": "%s",
                            "projectDescription": "%s",
                            "startDate": "%s",
                            "plannedEndDate": "%s",
                            "actualEndDate": "%s"
                            }
                            """, projectName, projectleadId, customerId, customerContact, projectDescription, startDate, plannedEndDate, actualEndDate))
            );

            verify(projectApplicationService, times(1)).updateProject(validUpdate, projectToUpdateId, dummyJwt, false);
        }

        @Test
        @DisplayName("properly recognize true force flag")
        public void forceFlagTrue() throws Exception {
            UpdateProjectCommand validUpdate = aDefaultUpdateCommand().build();
            ProjectId projectToUpdateId = new ProjectId(projectId);

            mockMvc.perform(put("/api/v1/project/" + projectToUpdateId.unbox() + "/?isForced=true")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", dummyJwt.jwt())
                    .content(String.format("""
                            {
                            "projectName": "%s",
                            "projectLeadId": %d,
                            "customerId": %d,
                            "customerContact": "%s",
                            "projectDescription": "%s",
                            "startDate": "%s",
                            "plannedEndDate": "%s",
                            "actualEndDate": "%s"
                            }
                            """, projectName, projectleadId, customerId, customerContact, projectDescription, startDate, plannedEndDate, actualEndDate))
            );

            verify(projectApplicationService, times(1)).updateProject(validUpdate, projectToUpdateId, dummyJwt, true);
        }

        @Test
        @DisplayName("not accept gibberish as force flag")
        public void forceFlagGibberish() throws Exception {

            ResultActions result = mockMvc.perform(put("/api/v1/project/" + projectId + "/?isForced=gibberish")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", dummyJwt.jwt())
                    .content(String.format("""
                            {
                            "projectName": "%s",
                            "projectLeadId": %d,
                            "customerId": %d,
                            "customerContact": "%s",
                            "projectDescription": "%s",
                            "startDate": "%s",
                            "plannedEndDate": "%s",
                            "actualEndDate": "%s"
                            }
                            """, projectName, projectleadId, customerId, customerContact, projectDescription, startDate, plannedEndDate, actualEndDate))
            );

            result.andExpect(status().isBadRequest());
        }

        @NonNull
        private UpdateProjectCommand.UpdateProjectCommandBuilder aDefaultUpdateCommand() {
            return UpdateProjectCommand.builder()
                    .projectName(Optional.of(new ProjectName(projectName)))
                    .projectLeadId(Optional.of(new ProjectLeadId(projectleadId)))
                    .customerId(Optional.of(new CustomerId(customerId)))
                    .customerContact(Optional.of(new CustomerContact(customerContact)))
                    .projectDescription(Optional.of(new ProjectDescription(projectDescription)))
                    .startDate(Optional.of(new StartDate(startDate)))
                    .plannedEndDate(Optional.of(new PlannedEndDate(plannedEndDate)))
                    .actualEndDate(Optional.of(new ActualEndDate(actualEndDate)));
        }

    }
}