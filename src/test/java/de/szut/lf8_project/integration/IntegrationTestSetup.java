package de.szut.lf8_project.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.szut.lf8_project.common.JWT;
import de.szut.lf8_project.domain.employee.EmployeeId;
import de.szut.lf8_project.repository.EmployeeRepoDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest
@ContextConfiguration(initializers = {IntegrationTestSetup.Initializer.class})
public class IntegrationTestSetup {

    @Autowired
    protected MockMvc mockMvc;

    @Container
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:13.3");

    @Autowired
    private Environment env;
    protected static JWT jwt;
    private static boolean setUpIsDone = false;
    private final List<Object> objectsToBeClearedAfterTest = new ArrayList<>();

    @BeforeEach
    public void setUpJwt() throws JsonProcessingException {
        if (setUpIsDone) return;
        jwt = getFreshJwt();
        setUpIsDone = true;
    }

    @AfterEach
    public void clearObjects() {
        objectsToBeClearedAfterTest.forEach(thing -> {
            if(thing instanceof EmployeeId) {
                deleteEmployeeInRemoteRepository((EmployeeId) thing);
            }
        });
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

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
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

    protected EmployeeId createEmployeeInRemoteRepository() {
        String jsonBody = String.format("""
                {
                  "firstName": "Testnutzer für Integrationstest",
                  "lastName": "%s",
                  "street": "Teststr",
                  "postcode": "28282",
                  "city": "Bremen",
                  "phone": "0111778899",
                  "skillSet": []
                }
                """, UUID.randomUUID());
        EmployeeRepoDto rawEmployee = new RestTemplate()
                .postForEntity(
                        Objects.requireNonNull(env.getProperty("employeeapi.baseUrl")),
                        buildPostRequest(jsonBody),
                        EmployeeRepoDto.class)
                .getBody();

        EmployeeId employeeId = new EmployeeId(rawEmployee.getId());
        objectsToBeClearedAfterTest.add(employeeId);


        return employeeId;
    }

    private HttpEntity<String> buildPostRequest(String jsonBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", jwt.jwt());

        return new HttpEntity<>(jsonBody, headers);
    }

}
