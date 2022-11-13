package de.szut.lf8_project.controller;


import de.szut.lf8_project.application.ApplicationServiceException;
import de.szut.lf8_project.application.ProjectApplicationService;
import de.szut.lf8_project.common.ErrorDetail;
import de.szut.lf8_project.common.Errorcode;
import de.szut.lf8_project.common.FailureMessage;
import de.szut.lf8_project.common.JWT;
import de.szut.lf8_project.controller.ProblemDetails.ProblemDetails;
import de.szut.lf8_project.controller.dtos.AddEmployeeCommand;
import de.szut.lf8_project.controller.dtos.CreateProjectCommand;
import de.szut.lf8_project.controller.dtos.ProjectView;
import de.szut.lf8_project.controller.dtos.UpdateProjectCommand;
import de.szut.lf8_project.domain.adapter.OpenApiProjectController;
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

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/{id}")
    public ResponseEntity<ProjectView> getProjectById(
            @Valid @PathVariable Long id
    ) {
        return new ResponseEntity<>(projectApplicationService.getProjectView(new ProjectId(id)), HttpStatus.OK);
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

    @DeleteMapping("/{projectId}")
    public ResponseEntity deleteProject(
            @Valid @PathVariable Long projectId
    ) {
        projectApplicationService.deleteProject(new ProjectId(projectId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = {"/{projectId}", "/{projectId}/{isForced}"})
    public ResponseEntity<ProjectView> updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody UpdateProjectCommand updateProjectCommand,
            @PathVariable(required = false) boolean isForced,
            @RequestHeader("Authorization") String authHeader
    ) {
        return new ResponseEntity<>(
                projectApplicationService.updateProject(updateProjectCommand, new ProjectId(projectId), new JWT(authHeader), Boolean.TRUE.equals(isForced)),
                HttpStatus.OK);
    }


    @ExceptionHandler
    public ResponseEntity<ProblemDetails> serializeApplicationServiceException(ApplicationServiceException ex, WebRequest request) {
        return new ResponseEntity<>(
                ProblemDetails.fromErrorDetail(ex.getErrorDetail()),
                ex.getErrorDetail().getErrorCode().getHttpRepresentation());
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetails> serializeInvalidParamsException(BindException ex, WebRequest request) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        String msg = String.join("\n", errorMap.entrySet().stream().map(Object::toString).toList());
        return new ResponseEntity<>(
                ProblemDetails.fromErrorDetail(new ErrorDetail(
                        Errorcode.INVALID_REQUEST_PARAMETER,
                        new FailureMessage(
                                msg.isBlank() ?
                                        "Your request contains invalid parameter. Please check if your field types are correct" :
                                        msg)
                )),
                Errorcode.INVALID_REQUEST_PARAMETER.getHttpRepresentation());
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetails> serializeNotReadableException(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>(
                ProblemDetails.fromErrorDetail(new ErrorDetail(
                        Errorcode.INVALID_REQUEST_PARAMETER,
                        new FailureMessage("Your request could not be read or parsed" )
                )),
                Errorcode.INVALID_REQUEST_PARAMETER.getHttpRepresentation());
    }
}