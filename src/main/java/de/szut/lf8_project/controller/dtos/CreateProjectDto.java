package de.szut.lf8_project.controller.dtos;

import java.time.LocalDate;

public record CreateProjectDto(
    String projectName,
    String projectDescription,
    Long projectLead,
    Long customerId,
    Long contactPersonId,
    LocalDate startDate,
    LocalDate plannedEndDate
    ) {}
