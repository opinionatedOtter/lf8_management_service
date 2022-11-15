package de.szut.lf8_project.controller;

import de.szut.lf8_project.application.ApplicationServiceException;
import de.szut.lf8_project.common.ErrorDetail;
import de.szut.lf8_project.common.Errorcode;
import de.szut.lf8_project.common.FailureMessage;
import de.szut.lf8_project.controller.ProblemDetails.ProblemDetails;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandling {

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

    @ExceptionHandler({HttpMessageNotReadableException.class, TypeMismatchException.class})
    public ResponseEntity<ProblemDetails> genericBadRequest() {
        return new ResponseEntity<>(
                ProblemDetails.fromErrorDetail(new ErrorDetail(
                        Errorcode.INVALID_REQUEST_PARAMETER,
                        new FailureMessage("Your request could not be read or parsed")
                )),
                Errorcode.INVALID_REQUEST_PARAMETER.getHttpRepresentation());
    }
}
