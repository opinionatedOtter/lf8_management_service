package de.szut.lf8_project.controller;


import de.szut.lf8_project.application.ApplicationServiceException;
import de.szut.lf8_project.application.EmployeeApplicationService;
import de.szut.lf8_project.common.ErrorDetail;
import de.szut.lf8_project.common.Errorcode;
import de.szut.lf8_project.common.FailureMessage;
import de.szut.lf8_project.controller.ProblemDetails.ProblemDetails;
import de.szut.lf8_project.controller.dtos.EmployeesOfProjectView;
import de.szut.lf8_project.domain.adapter.OpenApiEmployeeController;
import de.szut.lf8_project.domain.project.ProjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/employee", produces = {MediaType.APPLICATION_JSON_VALUE})
public class EmployeeController implements OpenApiEmployeeController {

    private final EmployeeApplicationService employeeApplicationService;

    public EmployeeController(final EmployeeApplicationService employeeApplicationService) {
        this.employeeApplicationService = employeeApplicationService;
    }

    @GetMapping("/byProject/{id}")
    public ResponseEntity<EmployeesOfProjectView> getEmployeesByProject(
            @Valid @PathVariable Long id
    ) {
        return new ResponseEntity<>(employeeApplicationService.getEmployeesOfProject(new ProjectId(id)), HttpStatus.OK);
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
                        new FailureMessage("Your request could not be read or parsed")
                )),
                Errorcode.INVALID_REQUEST_PARAMETER.getHttpRepresentation());
    }
}