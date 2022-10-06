package de.szut.lf8_project.repository;

import de.szut.lf8_project.common.JWT;
import de.szut.lf8_project.domain.EmployeeId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class TestEmployeeRestRepository {

    @Autowired
    private EmployeeRestRepository employeeRestRepository;

    @Test
    @DisplayName("Gets a Employee by ID")
    public void getEmployee() throws RepositoryException {
        String jwt = "";

        assertThrows(RepositoryException.class, () -> employeeRestRepository.getEmployeeById(new JWT(""), new EmployeeId(1L)));
    }

}
