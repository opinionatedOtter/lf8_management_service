package de.szut.lf8_project.controller.dtos;

import de.szut.lf8_project.domain.customer.Customer;
import de.szut.lf8_project.domain.project.*;

public class ProjectView {

    private ProjectId projectId;
    private ProjectName projectName;
    private ProjectDescription projectDescription;
    private ProjectLead projectLead;
    private Customer customer;
    private StartDate startDate;
    private PlannedEndDate plannedEndDate;
    private ActualEndDate actualEndDate;
}