package de.szut.lf8_project.controller;


import de.szut.lf8_project.application.EmployeeApplicationService;
import de.szut.lf8_project.controller.dtos.EmployeesOfProjectView;
import de.szut.lf8_project.domain.adapter.OpenApiEmployeeController;
import de.szut.lf8_project.domain.project.ProjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/employee", produces = {MediaType.APPLICATION_JSON_VALUE})
public class EmployeeController implements OpenApiEmployeeController {

    private final EmployeeApplicationService employeeApplicationService;

    public EmployeeController(final EmployeeApplicationService employeeApplicationService) {
        this.employeeApplicationService = employeeApplicationService;
    }

    @GetMapping("/byProject/{projectId}")
    public ResponseEntity<EmployeesOfProjectView> getEmployeesByProject(
            @Valid @PathVariable Long projectId
    ) {
        return new ResponseEntity<>(employeeApplicationService.getEmployeesOfProject(new ProjectId(projectId)), HttpStatus.OK);
    }

}