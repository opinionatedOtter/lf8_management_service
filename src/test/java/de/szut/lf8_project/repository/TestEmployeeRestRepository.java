package de.szut.lf8_project.repository;

import de.szut.lf8_project.common.JWT;
import de.szut.lf8_project.common.Statuscode;
import de.szut.lf8_project.domain.Employee.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class TestEmployeeRestRepository {

    private final String baseUrl = "example.org";
    private final JWT jwt = new JWT("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
    private final Long defaultId = 1L;
    private EmployeeRestRepository employeeRestRepository;
    private HttpHeaders header;
    @Mock
    private RestTemplate mockTemplate;

    @BeforeEach
    public void setUp() {
        employeeRestRepository = new EmployeeRestRepository(baseUrl, mockTemplate);
        header = new HttpHeaders();
        header.set("Authorization", jwt.jwt());
    }

    @Test
    @DisplayName("gets a employee by ID")
    public void getEmployee() throws RepositoryException {
        EmployeeRepoDto employeeDto = aDefaultEmployeeDto();
        Employee expectedEmployee = aDefaultEmployee();
        when(mockTemplate.exchange(baseUrl + employeeDto.getId(), HttpMethod.GET, new HttpEntity<String>(header), EmployeeRepoDto.class))
                .thenReturn(new ResponseEntity<>(employeeDto, HttpStatus.OK));

        Employee employee = employeeRestRepository.getEmployeeById(jwt, new EmployeeId(employeeDto.getId()));

        assertThat(employee).isEqualTo(expectedEmployee);
    }

    @Test
    @DisplayName("throws a exception with an 401 statuscode if the jwt is invalid")
    public void unauthorized() {
        when(mockTemplate.exchange(baseUrl + defaultId, HttpMethod.GET, new HttpEntity<String>(header), EmployeeRepoDto.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        RepositoryException exception = assertThrows(RepositoryException.class, () -> employeeRestRepository.getEmployeeById(jwt, new EmployeeId(defaultId)));

        assertEquals(Statuscode.UNAUTHORIZED, exception.getStatuscode());
    }

    @Test
    @DisplayName("throws a exception with an 404 statuscode if no employee is known by the id")
    public void employee404() {
        when(mockTemplate.exchange(baseUrl + defaultId, HttpMethod.GET, new HttpEntity<String>(header), EmployeeRepoDto.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        RepositoryException exception = assertThrows(RepositoryException.class, () -> employeeRestRepository.getEmployeeById(jwt, new EmployeeId(defaultId)));

        assertEquals(Statuscode.NOT_FOUND, exception.getStatuscode());
    }

    @Test
    @DisplayName("throws a exception with an 500 statuscode if there is an unexpected error")
    public void unexpectedError() {
        when(mockTemplate.exchange(baseUrl + defaultId, HttpMethod.GET, new HttpEntity<String>(header), EmployeeRepoDto.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        RepositoryException exception = assertThrows(RepositoryException.class, () -> employeeRestRepository.getEmployeeById(jwt, new EmployeeId(defaultId)));

        assertEquals(Statuscode.INTERNAL_SERVER_ERROR, exception.getStatuscode());
    }

    private EmployeeRepoDto aDefaultEmployeeDto() {
        return EmployeeRepoDto.builder()
                .id(defaultId)
                .firstName("Tester")
                .lastName("Mester")
                .postcode("28311")
                .street("Am Graben 26")
                .skillset(List.of("Schubsen", "Tanzen", "Smalltalk"))
                .build();
    }

    private Employee aDefaultEmployee() {
        return Employee.builder()
                .id(new EmployeeId(defaultId))
                .firstName(new FirstName("Tester"))
                .lastName(new LastName("Mester"))
                .postcode(new Postcode("28311"))
                .street(new Street("Am Graben 26"))
                .skillset(List.of(new Qualification("Schubsen"), new Qualification("Tanzen"), new Qualification("Smalltalk")))
                .build();
    }

}
