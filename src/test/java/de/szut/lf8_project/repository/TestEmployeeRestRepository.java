package de.szut.lf8_project.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.szut.lf8_project.common.JWT;
import de.szut.lf8_project.common.Statuscode;
import de.szut.lf8_project.domain.Employee;
import de.szut.lf8_project.domain.EmployeeId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class TestEmployeeRestRepository {

    private static boolean setUpIsDone = false;
    @Autowired
    private EmployeeRestRepository employeeRestRepository;
    @Autowired
    private Environment env;
    private JWT jwt;

    @BeforeEach
    public void setUpJwt() throws JsonProcessingException {
        if (setUpIsDone) return;
        this.jwt = getFreshJwt();
        setUpIsDone = true;
    }


    // 500er test
    // create query für stuff?
    // commit für bean config

    @Test
    @DisplayName("gets a employee by ID")
    public void getEmployee() throws RepositoryException {
        //TODO - umbauen sobald wir Kontrolle über den Service haben oder Insert-Hilfsmethode für Insert schreiben
        EmployeeId employeeId = new EmployeeId(115L);

        Employee employee = employeeRestRepository.getEmployeeById(jwt, employeeId);

        assertThat(employee.id()).isEqualTo(employeeId);
    }

    @Test
    @DisplayName("throws a exception with an 401 statuscode if the jwt is invalid")
    public void unauthorized() {
        JWT jwt = new JWT("invalid");

        RepositoryException exception = assertThrows(RepositoryException.class, () -> employeeRestRepository.getEmployeeById(jwt, new EmployeeId(1L)));

        assertEquals(Statuscode.UNAUTHORIZED, exception.getStatuscode());
    }

    @Test
    @DisplayName("throws a exception with an 404 statuscode if no employee is known by the id")
    public void employee404() {
        //TODO - umbauen sobald wir Kontrolle über den Service haben oder Insert-Hilfsmethode für Insert schreiben
        EmployeeId employeeId = new EmployeeId(9999L);

        RepositoryException exception = assertThrows(RepositoryException.class, () -> employeeRestRepository.getEmployeeById(jwt, employeeId));

        assertEquals(Statuscode.NOT_FOUND, exception.getStatuscode());
    }

    @Test
    @DisplayName("throws a exception with an 500 statuscode if there is an unexpected error")
    public void unexpectedError() throws RepositoryException {
        EmployeeId employeeId = new EmployeeId(115L);

        Employee employee = employeeRestRepository.getEmployeeById(jwt, employeeId);

        assertThat(employee.id()).isEqualTo(employeeId);
    }

    private JWT getFreshJwt() throws JsonProcessingException {
        String jsonString = new RestTemplate()
                .postForEntity(
                        Objects.requireNonNull(env.getProperty("authProvider.url")),
                        getPostRequestBody(),
                        String.class)
                .getBody();
        Map<String, String> map = new ObjectMapper().readValue(jsonString, Map.class);
        return new JWT("Bearer " + map.get("access_token"));
    }

    private HttpEntity<MultiValueMap<String, String>> getPostRequestBody() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> bodyParamMap = new LinkedMultiValueMap<>();
        bodyParamMap.add("grant_type", "password");
        bodyParamMap.add("client_id", env.getProperty("authProvider.client_id"));
        bodyParamMap.add("username", env.getProperty("authProvider.user"));
        bodyParamMap.add("password", env.getProperty("authProvider.password"));
        return new HttpEntity<>(bodyParamMap, headers);
    }

}
