package de.szut.lf8_project.repository;

import de.szut.lf8_project.common.Errorcode;
import de.szut.lf8_project.common.JWT;
import de.szut.lf8_project.domain.employee.*;
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
@DisplayName("The EmployeeRestRepository should")
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
        employeeRestRepository = new EmployeeRestRepository(baseUrl, mockTemplate, new EmployeeMapper());
        header = new HttpHeaders();
        header.set("Authorization", jwt.jwt());
    }

    @Test
    @DisplayName("get an employee by ID")
    public void getEmployee() throws RepositoryException {
        EmployeeData employeeDto = aDefaultEmployeeDto();
        Employee expectedEmployee = aDefaultEmployee();
        when(mockTemplate.exchange(baseUrl + employeeDto.getId(), HttpMethod.GET, new HttpEntity<String>(header), EmployeeData.class))
                .thenReturn(new ResponseEntity<>(employeeDto, HttpStatus.OK));

        Employee employee = employeeRestRepository.getEmployeeById(jwt, new EmployeeId(employeeDto.getId()));

        assertThat(employee).isEqualTo(expectedEmployee);
    }

    @Test
    @DisplayName("throw an exception with errorcode 401 if the jwt is invalid")
    public void unauthorized() {
        when(mockTemplate.exchange(baseUrl + defaultId, HttpMethod.GET, new HttpEntity<String>(header), EmployeeData.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        RepositoryException exception = assertThrows(RepositoryException.class, () -> employeeRestRepository.getEmployeeById(jwt, new EmployeeId(defaultId)));

        assertEquals(Errorcode.UNAUTHORIZED, exception.getErrorDetail().getErrorCode());
    }

    @Test
    @DisplayName("throw an exception with errorcode 404 if no employee is known by the id")
    public void employee404() {
        when(mockTemplate.exchange(baseUrl + defaultId, HttpMethod.GET, new HttpEntity<String>(header), EmployeeData.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        RepositoryException exception = assertThrows(RepositoryException.class, () -> employeeRestRepository.getEmployeeById(jwt, new EmployeeId(defaultId)));

        assertEquals(Errorcode.ENTITY_NOT_FOUND, exception.getErrorDetail().getErrorCode());
    }

    @Test
    @DisplayName("throw an exception with errorcode 500 if there is an unexpected error")
    public void unexpectedError() {
        when(mockTemplate.exchange(baseUrl + defaultId, HttpMethod.GET, new HttpEntity<String>(header), EmployeeData.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        RepositoryException exception = assertThrows(RepositoryException.class, () -> employeeRestRepository.getEmployeeById(jwt, new EmployeeId(defaultId)));

        assertEquals(Errorcode.UNEXPECTED_ERROR, exception.getErrorDetail().getErrorCode());
    }

    private EmployeeData aDefaultEmployeeDto() {
        return EmployeeData.builder()
                .id(defaultId)
                .firstName("Tester")
                .lastName("Mester")
                .postcode("28311")
                .city("Bremen")
                .phone("0421 22 33 44")
                .street("Am Graben 26")
                .skillSet(List.of("Schubsen", "Tanzen", "Smalltalk"))
                .build();
    }

    private Employee aDefaultEmployee() {
        return Employee.builder()
                .id(new EmployeeId(defaultId))
                .firstName(new FirstName("Tester"))
                .lastName(new LastName("Mester"))
                .postcode(new Postcode("28311"))
                .city(new City("Bremen"))
                .phonenumber(new Phonenumber("0421 22 33 44"))
                .street(new Street("Am Graben 26"))
                .skillset(List.of(new ProjectRole("Schubsen"), new ProjectRole("Tanzen"), new ProjectRole("Smalltalk")))
                .build();
    }

}
