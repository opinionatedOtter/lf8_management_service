package de.szut.lf8_project.domain.project;

import de.szut.lf8_project.domain.customer.Customer;
import de.szut.lf8_project.domain.employee.Employee;
import lombok.Builder;
import lombok.Value;

import java.util.Date;
import java.util.Set;


@Builder
@Value
public class Project {
    private ProjectId projectId;
    private ProjectName projectName;
    private ProjectDescription projectDescription;
    private ProjectLead projectLead;
    private Customer customer;
    private StartDate startDate;
    private PlannedEndDate plannedEndDate;
    private ActualEndDate actualEndDate;
    private Set<TeamMember> teamMembers;
}