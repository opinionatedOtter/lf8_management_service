package de.szut.lf8_project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import de.szut.lf8_project.common.JWT;
import de.szut.lf8_project.domain.customer.Customer;
import de.szut.lf8_project.domain.customer.CustomerId;
import de.szut.lf8_project.domain.employee.*;
import de.szut.lf8_project.domain.project.*;
import de.szut.lf8_project.repository.EmployeeData;
import de.szut.lf8_project.repository.EmployeeMapper;
import de.szut.lf8_project.repository.RepositoryException;
import de.szut.lf8_project.repository.projectRepository.ProjectRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@AutoConfigureMockMvc
@SpringBootTest
public abstract class FullIntegrationTest extends WithAppContextContainerTest {

    protected static JWT jwt;
    private static boolean setUpIsDone = false;
    private final List<Object> objectsToBeClearedAfterTest = new ArrayList<>();
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private Environment env;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private EmployeeMapper employeeMapper;

    @BeforeEach
    public void setUpJwt() throws JsonProcessingException {
        if (setUpIsDone) return;
        jwt = getFreshJwt();
        setUpIsDone = true;
    }

    @AfterEach
    public void afterEach() {
        clearObjects();
    }

    private void clearObjects() {
        objectsToBeClearedAfterTest.forEach(thing -> {
            if (thing instanceof EmployeeId) {
                deleteEmployeeInRemoteRepository((EmployeeId) thing);
            }
        });

        objectsToBeClearedAfterTest.forEach(thing -> {
            if (thing instanceof ProjectRoleFromJson) {
                deleteQualificationInRemoteRepository((ProjectRoleFromJson) thing);
            }
        });
    }

    private void deleteQualificationInRemoteRepository(final ProjectRoleFromJson role) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", jwt.jwt());
        headers.setContentType(MediaType.APPLICATION_JSON);
        RequestEntity<String> requestEntity = new RequestEntity<>(
                "{\"skill\":\"" + role.skill + "\"}",
                headers,
                HttpMethod.DELETE,
                URI.create(env.getProperty("qualificationsapi.baseUrl"))
        );

        new RestTemplate().exchange(requestEntity, String.class);
    }

    private void deleteEmployeeInRemoteRepository(final EmployeeId employeeId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", jwt.jwt());
        RequestEntity<String> requestEntity = new RequestEntity<>(
                headers,
                HttpMethod.DELETE,
                URI.create(env.getProperty("employeeapi.baseUrl") + employeeId.toString())
        );

        new RestTemplate().exchange(requestEntity, String.class);
    }

    private JWT getFreshJwt() throws JsonProcessingException {
        String jsonString = new RestTemplate()
                .postForEntity(
                        Objects.requireNonNull(env.getProperty("authProvider.url")),
                        getLoginBody(),
                        String.class)
                .getBody();
        Map<String, String> map = new ObjectMapper().readValue(jsonString, Map.class);
        return new JWT("Bearer " + map.get("access_token"));
    }

    private HttpEntity<MultiValueMap<String, String>> getLoginBody() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> bodyParamMap = new LinkedMultiValueMap<>();
        bodyParamMap.add("grant_type", "password");
        bodyParamMap.add("client_id", env.getProperty("authProvider.client_id"));
        bodyParamMap.add("username", env.getProperty("authProvider.user"));
        bodyParamMap.add("password", env.getProperty("authProvider.password"));
        return new HttpEntity<>(bodyParamMap, headers);
    }



    protected Employee createDefaultEmployeeWith0Id() {
        return createDefaultEmployeeWithRolesWith0Id(Collections.emptyList());
    }

    protected Employee createDefaultEmployeeWithRolesWith0Id(List<ProjectRole> projectRoles) {
        return new Employee(
                new EmployeeId(0L),
                new LastName(UUID.randomUUID().toString()),
                new FirstName("Testnutzer mit Qualifikation für Integrationstest"),
                new Street("Teststr."),
                new City("Bremen"),
                new Postcode("28282"),
                new Phonenumber("0111778899"),
                projectRoles
        );
    }


    protected Project createDefaultProject(final ProjectLeadId projectLeadId) {
        Project project = Project.builder()
                .projectId(Optional.empty())
                .projectName(new ProjectName("Name"))
                .projectDescription(Optional.of(new ProjectDescription("Beschreibung")))
                .projectLead(new ProjectLead(projectLeadId))
                .customer(new Customer(new CustomerId(16L)))
                .customerContact(new CustomerContact("Franz-Ferdinand Falke"))
                .startDate(Optional.of(new StartDate(LocalDate.of(2022, 1, 20))))
                .plannedEndDate(Optional.of(new PlannedEndDate(LocalDate.of(2022, 4, 24))))
                .actualEndDate(Optional.of(new ActualEndDate(LocalDate.of(2022, 6, 26))))
                .teamMembers(Collections.emptySet())
                .build();
        return project;
    }

    protected Employee saveEmployeeInRemoteRepository(Employee employee) {
        String jsonBody = String.format("""
                {
                  "firstName": "%s",
                  "lastName": "%s",
                  "street": "%s",
                  "postcode": "%s",
                  "city": "%s",
                  "phone": "%s",
                  "skillSet": [%s]
                }
                """,
                employee.getFirstName().unbox(),
                employee.getLastName().unbox(),
                employee.getStreet().unbox(),
                employee.getPostcode().unbox(),
                employee.getCity().unbox(),
                employee.getPhonenumber().unbox(),
                employee.getSkillset().stream().map(projectRole -> "\"" + projectRole.unbox() + "\"").collect(Collectors.joining(","))
                );
        EmployeeData rawEmployee = new RestTemplate()
                .postForEntity(
                        Objects.requireNonNull(env.getProperty("employeeapi.baseUrl")),
                        buildRequestEntity(jsonBody),
                        EmployeeData.class)
                .getBody();

        EmployeeId employeeId = new EmployeeId(rawEmployee.getId());
        objectsToBeClearedAfterTest.add(employeeId);


        return employeeMapper.dtoToEntity(rawEmployee);
    }

    protected ProjectRole createAndSaveQualificationInRemoteRepository() {
        ProjectRole projectRole = new ProjectRole(
                "Skill " + UUID.randomUUID()
        );

        ResponseEntity<ProjectRoleFromJson> transferProjectRoleResponseEntity = new RestTemplate()
                .postForEntity(
                        Objects.requireNonNull(env.getProperty("qualificationsapi.baseUrl")),
                        buildRequestEntity("{\"skill\": \"" + projectRole.unbox() + "\"}"),
                        ProjectRoleFromJson.class);

        ProjectRoleFromJson transfer = transferProjectRoleResponseEntity.getBody();
        objectsToBeClearedAfterTest.add(transfer);

        return new ProjectRole(transfer.skill);

    }

    protected Project saveProjectInDatabase(Project project) throws RepositoryException {

        return projectRepository.saveProject(project);
    }

    protected Project createAndSaveDefaultProjectWithProjectLead() throws RepositoryException {
        Employee projectLead = saveEmployeeInRemoteRepository(createDefaultEmployeeWith0Id());
        return saveProjectInDatabase(createDefaultProject(new ProjectLeadId(projectLead.getId().unbox())));
    }

    protected Project getProjectByIdFromDatabase(ProjectId projectId) throws RepositoryException {
        return projectRepository.getProject(projectId);
    }

    private HttpEntity<String> buildRequestEntity(String jsonBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", jwt.jwt());

        return new HttpEntity<>(jsonBody, headers);
    }

    protected ProjectId getProjectIdFromMvcJsonResponse(MvcResult result) throws UnsupportedEncodingException {
        Integer idAsString = JsonPath.read(result.getResponse().getContentAsString(), "$.projectId");
        return new ProjectId(Long.valueOf(idAsString));

    }
}
