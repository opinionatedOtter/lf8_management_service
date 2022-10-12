package de.szut.lf8_project.domain.adapter;

import de.szut.lf8_project.common.JWT;
import de.szut.lf8_project.domain.Employee.Employee;
import de.szut.lf8_project.domain.Employee.EmployeeId;
import de.szut.lf8_project.repository.RepositoryException;

public interface EmployeeRepository {

    Employee getEmployeeById(JWT jwt, EmployeeId employeeId) throws RepositoryException;
}
