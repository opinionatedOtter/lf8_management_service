package de.szut.lf8_project.controller.dtos;

import java.util.Date;

public class CreateProjectDto {
    private Long projectId;
    private String projectName;
    private String projectDescription;
    private Long projectLead;
    private Long customerId;
    private Date startDate;
    private Date plannedEndDate;
    private Date actualEndDate;
}