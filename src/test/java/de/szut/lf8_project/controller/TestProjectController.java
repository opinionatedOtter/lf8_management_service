package de.szut.lf8_project.controller;

import de.szut.lf8_project.application.ProjectApplicationService;
import de.szut.lf8_project.common.JWT;
import de.szut.lf8_project.controller.dtos.CreateProjectCommand;
import de.szut.lf8_project.controller.dtos.ProjectView;
import de.szut.lf8_project.domain.customer.Customer;
import de.szut.lf8_project.domain.customer.CustomerId;
import de.szut.lf8_project.domain.project.CustomerContact;
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

    private JWT dummyJwt = new JWT("Bearer fake.fake.fake");


    // TODO: crashed wegen fehelender Authentication
    @Test
    @DisplayName("accept valid json and return the created entity")
    public void acceptValidJsonHappyPath() throws Exception {
        CreateProjectCommand validProject = new CreateProjectCommand(
                new ProjectName("foobar"),
                new ProjectLeadId(456L),
                new CustomerId(789L),
                new CustomerContact("Testkontakt"),
                Optional.of(new ProjectDescription("foobar at the beach")),
                Optional.of(new StartDate(LocalDate.of(2022, 9, 23))),
                Optional.empty()
        );
        ProjectView projectView = ProjectView.builder()
                .projectId(new ProjectId(123L))
                .projectDescription(Optional.of(new ProjectDescription("foobar at the beach")))
                .projectLead(new ProjectLead(new ProjectLeadId(456L)))
                .projectName(new ProjectName("foobar"))
                .customer(new Customer(new CustomerId(789L)))
                .actualEndDate(Optional.empty())
                .plannedEndDate(Optional.of(new PlannedEndDate(LocalDate.of(2022, 11, 15))))
                .startDate(Optional.of(new StartDate(LocalDate.of(2022, 9, 23))))
                .build();
        when(projectApplicationService.createProject(validProject, dummyJwt)).thenReturn(projectView);

        ResultActions result = mockMvc.perform(post("/api/v1/project")



                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", dummyJwt.jwt())
                .content("""
                        {
                        "projectName": "foobar",
                        "projectLeadId": 456,
                        "customerId": 789,
                        "customerContact": "Testkontakt",
                        "projectDescription": "foobar at the beach",
                        "startDate": "2022-09-23"
                        }
                        """)
        );

        result.andExpect(content()
                .json("""
                            {
                            "projectId": 123,
                            "projectName": "foobar",
                            "projectDescription": "foobar at the beach",
                            "startDate": "2022-09-23",
                             "projectLead": {
                                "projectLeadId": 456
                            },
                            "customer" : {
                                "customerId": 789
                                }
                            }
                        """)
        ).andExpect(status().isCreated());


    }
}