package de.szut.lf8_project.domain.Project;

import de.szut.lf8_project.domain.Employee.Employee;
import lombok.Data;
import lombok.Value;

import javax.persistence.Table;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Value
@Table
public class Project {
    private ProjectID projectID;
    private ProjectName projectName;
    private ProjectDescription projectDescription;
    private Employee responsibleEmployee;
    private Customer customer;
    private Date startDate;
    private Date plannedEndDate;
    private Date actualEndDate;
    private Set<Employee> assignedEmployees;
}