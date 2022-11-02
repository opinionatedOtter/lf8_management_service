package de.szut.lf8_project.controller.dtos;

import java.util.Date;

public record CreateProjectDto(
    String projectName,
    String projectDescription,
    Long projectLead,
    Long customerId,
    Long contactPersonId,
    Date startDate,
    Date plannedEndDate
    ) {}
