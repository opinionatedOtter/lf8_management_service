package de.szut.lf8_project.controller;

import de.szut.lf8_project.application.ProjectApplicationService;
import de.szut.lf8_project.controller.dtos.CreateProjectDto;
import de.szut.lf8_project.controller.dtos.ProjectView;
import de.szut.lf8_project.domain.customer.Customer;
import de.szut.lf8_project.domain.customer.CustomerId;
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

import java.util.Calendar;
import java.util.Date;

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

    @Test
    @DisplayName("accept valid json and return the created entity")
    public void acceptValidJsonHappyPath() throws Exception {

        CreateProjectDto validProject = new CreateProjectDto(
                "foobar",
                "foobar at the beach",
                456L,
                789L,
                new Date(2022, Calendar.SEPTEMBER, 23),
                new Date(2022, Calendar.NOVEMBER,15)
        );

        ProjectView projectView = new ProjectView(
                new ProjectId(123L),
                new ProjectName("foobar"),
                new ProjectDescription("foobar at the beach"),
                new ProjectLead(new ProjectLeadId(456L)),
                new Customer(new CustomerId(789L)),
                new StartDate(new Date(2022, Calendar.SEPTEMBER, 23)),
                new PlannedEndDate(new Date(2022, Calendar.NOVEMBER,15)),
                null
        );

        when(projectApplicationService.createProject(validProject)).thenReturn(projectView);

        ResultActions result = mockMvc.perform(post("/api/v1/project")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
{
"projectName": "foobar",
"projectDescription": "foobar at the beach",
"projectLead": 456,
"customerId": 789,
"startDate": "2022-09-23",
"plannedEndDate": "2022-11-15"
}
""")
        );
        result.andExpect(status().isOk()).andExpect(content().json("""
{
"projectName": "foobar",
"projectDescription": "foobar at the beach",
"projectLead": 456,
"customerId": 789,
"startDate": "2022-09-23",
"plannedEndDate": "2022-11-23"
}
""")
        );
}
}