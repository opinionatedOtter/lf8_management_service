package de.szut.lf8_project.repository;

import de.szut.lf8_project.common.ErrorDetail;
import de.szut.lf8_project.common.Errorcode;
import de.szut.lf8_project.common.FailureMessage;
import de.szut.lf8_project.common.JWT;
import de.szut.lf8_project.domain.adapter.EmployeeRepository;
import de.szut.lf8_project.domain.employee.*;
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
    private final EmployeeMapper employeeMapper;

    public EmployeeRestRepository(
            @Value("${employeeapi.baseUrl}") String baseUrl,
            RestTemplate restTemplate,
            final EmployeeMapper employeeMapper
    ) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
        this.employeeMapper = employeeMapper;
    }


    @Override
    public Employee getEmployeeById(JWT jwt, EmployeeId employeeId) throws RepositoryException {
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", jwt.jwt());
        try {
            return employeeMapper.dtoToEntity(
                    Objects.requireNonNull(
                            restTemplate.exchange(baseUrl + employeeId.unbox().toString(), HttpMethod.GET, new HttpEntity<String>(header), EmployeeData.class).getBody()
                    ));
        } catch (HttpClientErrorException e) {
            if (e.getRawStatusCode() == 404)
                throw new RepositoryException(new ErrorDetail(Errorcode.ENTITY_NOT_FOUND, new FailureMessage("The Employee with the ID " + employeeId.unbox() + " could not be found")));
            else if (e.getRawStatusCode() == 401)
                throw new RepositoryException(new ErrorDetail(Errorcode.UNAUTHORIZED, new FailureMessage("You are not authorized to use this service")));
            else
                throw new RepositoryException(new ErrorDetail(Errorcode.UNEXPECTED_ERROR, new FailureMessage("An unknown error occurred")));
        } catch (RestClientException e) {
            throw new RepositoryException(new ErrorDetail(Errorcode.UNEXPECTED_ERROR, new FailureMessage("An unknown error occurred")));
        }
    }
}
