package de.szut.lf8_project.repository;

import de.szut.lf8_project.common.JWT;
import de.szut.lf8_project.common.Statuscode;
import de.szut.lf8_project.domain.Employee;
import de.szut.lf8_project.domain.EmployeeId;
import de.szut.lf8_project.domain.adapter.EmployeeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Repository
public class EmployeeRestRepository implements EmployeeRepository {

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public EmployeeRestRepository(
            @Value("${employeeapi.baseUrl}") String baseUrl
    ) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }


    @Override
    public Employee getEmployeeById(JWT jwt, EmployeeId employeeId) throws RepositoryException {
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", jwt.jwt());
        try {
            return Employee.fromModel(
                    Objects.requireNonNull(
                            restTemplate.exchange(baseUrl + employeeId.unbox().toString(), HttpMethod.GET, new HttpEntity<String>(header), EmployeeRepoDto.class).getBody()
                    )
            );
        } catch (HttpClientErrorException e) {
            throw new RepositoryException(Statuscode.of(String.valueOf(e.getRawStatusCode())));
        } catch (RestClientException e) {
            throw new RepositoryException(Statuscode.INTERNAL_SERVER_ERROR);
        }
    }
}
