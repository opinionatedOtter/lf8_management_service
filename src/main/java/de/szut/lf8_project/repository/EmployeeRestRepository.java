package de.szut.lf8_project.repository;

import de.szut.lf8_project.common.JWT;
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

@Repository
public class EmployeeRestRepository implements EmployeeRepository {

    private RestTemplate restTemplate;

    private final String baseUrl;

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
            return restTemplate.exchange("https://employee.szut.dev/employees/1", HttpMethod.GET, new HttpEntity<String>(header), Employee.class).getBody();

        } catch (HttpClientErrorException e){
            throw new RepositoryException();
        } catch (RestClientException e){

        }
    }
}
