package de.szut.lf8_project.controller;


import de.szut.lf8_project.application.ProjectApplicationService;
import de.szut.lf8_project.common.JWT;
import de.szut.lf8_project.controller.dtos.AddEmployeeCommand;
import de.szut.lf8_project.controller.dtos.CreateProjectCommand;
import de.szut.lf8_project.controller.dtos.ProjectView;
import de.szut.lf8_project.controller.dtos.UpdateProjectCommand;
import de.szut.lf8_project.controller.dtos.*;
import de.szut.lf8_project.domain.adapter.OpenApiProjectController;
import de.szut.lf8_project.domain.employee.EmployeeId;
import de.szut.lf8_project.domain.project.ProjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/project", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.APPLICATION_JSON_VALUE)
public class ProjectController implements OpenApiProjectController {

    private final ProjectApplicationService projectApplicationService;

    public ProjectController(ProjectApplicationService projectApplicationService) {
        this.projectApplicationService = projectApplicationService;
    }

    @PostMapping()
    public ResponseEntity<ProjectView> createProject(
            @Valid @RequestBody CreateProjectCommand createProjectCommand,
            @RequestHeader("Authorization") String authHeader
    ) {
        return new ResponseEntity<>(projectApplicationService.createProject(createProjectCommand, new JWT(authHeader)), HttpStatus.CREATED);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectView> getProjectById(
            @Valid @PathVariable Long projectId
    ) {
        return new ResponseEntity<>(projectApplicationService.getProjectView(new ProjectId(projectId)), HttpStatus.OK);
    }

    @PostMapping("/{projectId}")
    public ResponseEntity<ProjectView> addEmployee(
            @Valid @PathVariable Long projectId,
            @Valid @RequestBody AddEmployeeCommand addEmployeeCommand,
            @RequestHeader("Authorization") String authHeader
    ) {
        return new ResponseEntity<>(projectApplicationService.addEmployee(addEmployeeCommand, new ProjectId(projectId), new JWT(authHeader)), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<ProjectView>> getAllProjects() {
        return new ResponseEntity<>(projectApplicationService.getAllProjects(), HttpStatus.OK);
    }

    @GetMapping("/byEmployee/{employeeId}")
    public ResponseEntity<EmployeeProjectViewWrapper> getAllProjectsOfEmployee(
            @Valid @PathVariable Long employeeId,
            @RequestHeader("Authorization") String authHeader
    ) {
        return new ResponseEntity<>(projectApplicationService.getAllProjectsOfEmployee(new EmployeeId(employeeId), new JWT(authHeader)), HttpStatus.OK);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity deleteProject(
            @Valid @PathVariable Long projectId
    ) {
        projectApplicationService.deleteProject(new ProjectId(projectId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = {"/{projectId}"})
    public ResponseEntity<ProjectView> updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody UpdateProjectCommand updateProjectCommand,
            @RequestParam (required = false)  boolean isForced,
            @RequestHeader("Authorization") String authHeader
    ) {
        return new ResponseEntity<>(
                projectApplicationService.updateProject(updateProjectCommand, new ProjectId(projectId), new JWT(authHeader), Boolean.TRUE.equals(isForced)),
                HttpStatus.OK);
    }

    @DeleteMapping("/{projectId}/removeEmployee/{employeeId}")
    public ResponseEntity removeEmployeeFromProject(
            @Valid @PathVariable Long projectId,
            @Valid @PathVariable Long employeeId
    ) {
        projectApplicationService.removeEmployee(
                new ProjectId(projectId),
                new EmployeeId(employeeId)
        );

        return new ResponseEntity(HttpStatus.OK);
    }
}