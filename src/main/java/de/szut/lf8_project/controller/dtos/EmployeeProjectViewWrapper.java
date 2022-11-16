package de.szut.lf8_project.controller.dtos;

import de.szut.lf8_project.domain.employee.EmployeeId;
import lombok.Value;

import java.util.List;

@Value
public class EmployeeProjectViewWrapper {

    EmployeeId employeeId;
    List<EmployeeProjectView> projects;
}
