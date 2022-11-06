package de.szut.lf8_project.controller;


import de.szut.lf8_project.application.ApplicationServiceException;
import de.szut.lf8_project.application.ProjectApplicationService;
import de.szut.lf8_project.common.ErrorDetail;
import de.szut.lf8_project.common.Errorcode;
import de.szut.lf8_project.common.FailureMessage;
import de.szut.lf8_project.common.JWT;
import de.szut.lf8_project.controller.ProblemDetails.ProblemDetails;
import de.szut.lf8_project.controller.dtos.CreateProjectCommand;
import de.szut.lf8_project.controller.dtos.ProjectView;
import de.szut.lf8_project.domain.adapter.OpenApiProjectController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.util.HashMap;
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


    @ExceptionHandler
    public ResponseEntity<ProblemDetails> serializeApplicationServiceException(ApplicationServiceException ex, WebRequest request) {
        return new ResponseEntity<>(
                ProblemDetails.fromErrorDetail(ex.getErrorDetail()),
                ex.getErrorDetail().getErrorCode().getHttpRepresentation());
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetails> serializeInvalidParamsException(MethodArgumentNotValidException ex, WebRequest request) {
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

}