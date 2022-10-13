package de.szut.lf8_project.controller;


import de.szut.lf8_project.application.ProjectApplicationService;
import de.szut.lf8_project.common.JWT;
import de.szut.lf8_project.controller.dtos.CreateProjectDto;
import de.szut.lf8_project.controller.dtos.ProjectView;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

@Validated
@RestController
@RequestMapping(value = "/api/v1/project", produces = {MediaType.APPLICATION_JSON_VALUE})
public class ProjectController {

    private final ProjectApplicationService projectApplicationService;

    public ProjectController(ProjectApplicationService projectApplicationService) {
        this.projectApplicationService = projectApplicationService;
    }

    @PostMapping("")
    public ResponseEntity<ProjectView> createProject(
            @RequestBody CreateProjectDto createProjectDto,
            @RequestHeader("Authorization") String authheader
    ) {
        // TODO: ExceptionHandler + Json Controlleradvice
        JWT jwt = getJwtFromHeader(authheader);
        return new ResponseEntity<>(projectApplicationService.createProject(createProjectDto, jwt), HttpStatus.CREATED);
    }

    private JWT getJwtFromHeader(String authheader) {
        throw new NotYetImplementedException();
    }

    @ExceptionHandler
    public ResponseEntity<Object> serializeApplicationServiceException(Exception ex, WebRequest request) {
        return new ResponseEntity<Object>(
                // hier JSONStuff
                // typisiere Exception
                new Object()
                , HttpStatus.NOT_FOUND);
    }

}