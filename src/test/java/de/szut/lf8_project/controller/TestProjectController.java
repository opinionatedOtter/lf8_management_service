package de.szut.lf8_project.controller;

import com.c4_soft.springaddons.test.security.context.support.WithMockJwt;
import de.szut.lf8_project.application.ProjectApplicationService;
import de.szut.lf8_project.common.JWT;
import de.szut.lf8_project.controller.dtos.CreateProjectCommand;
import de.szut.lf8_project.controller.dtos.ProjectView;
import de.szut.lf8_project.domain.customer.Customer;
import de.szut.lf8_project.domain.customer.CustomerId;
import de.szut.lf8_project.domain.project.CustomerContactId;
import de.szut.lf8_project.domain.project.PlannedEndDate;
import de.szut.lf8_project.domain.project.ProjectDescription;
import de.szut.lf8_project.domain.project.ProjectId;
import de.szut.lf8_project.domain.project.ProjectLead;
import de.szut.lf8_project.domain.project.ProjectLeadId;
import de.szut.lf8_project.domain.project.ProjectName;
import de.szut.lf8_project.domain.project.StartDate;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("A Project controller should")
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class TestProjectController {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProjectApplicationService projectApplicationService;

    private JWT dummyJwt = new JWT("unused");



    // TODO: crashed wegen fehelender Authentication
    @Test
    @DisplayName("accept valid json and return the created entity")
    public void acceptValidJsonHappyPath() throws Exception {
        CreateProjectCommand validProject = new CreateProjectCommand(
                new ProjectName("foobar"),
                new ProjectLeadId(456L),
                new CustomerId(789L),
                new CustomerContactId(666L),
                Optional.of(new ProjectDescription("foobar at the beach")),
                Optional.of(new StartDate(LocalDate.of(2022,9,23))),
                Optional.of(new PlannedEndDate(LocalDate.of(2022,11,15)))
        );
        ProjectView projectView = ProjectView.builder()
                        .projectId(new ProjectId(123L))
                        .projectDescription(new ProjectDescription("foobar at the beach"))
                        .projectLead(new ProjectLead(new ProjectLeadId(456L)))
                        .projectName(new ProjectName("foobar"))
                        .customer(new Customer(new CustomerId(789L)))
                        .actualEndDate(null)
                        .plannedEndDate(new PlannedEndDate(LocalDate.of(2022, 11, 15)))
                        .startDate(new StartDate(LocalDate.of(2022, 9, 23)))
        .build();


        when(projectApplicationService.createProject(validProject, dummyJwt)).thenReturn(projectView);

        ResultActions result = mockMvc.perform(post("/api/v1/project")



                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", dummyJwt.jwt())
                .content("""
                        {
                        "projectName": "foobar",
                        "projectDescription": "foobar at the beach",
                        "projectLead": 456,
                        "customerId": 789,
                        "contactPersonId": 666,
                        "startDate": "2022-09-23",
                        "plannedEndDate": "2022-11-15"
                        }
                        """)
        );

        result.andExpect(content()
                .json("""
                            {
                            "projectId": 123,
                            "projectName": "foobar",
                            "projectDescription": "foobar at the beach",
                            "projectLeadId": 456,
                            "customerId": 789,
                            "startDate": "2022-09-23",
                            "plannedEndDate": "2022-11-15"
                            }
                        """)
        ).andExpect(status().isCreated());
    }
}