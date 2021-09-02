package de.szut.lf8_project.hello;

import de.szut.lf8_project.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_project.hello.HelloCreateDto;
import de.szut.lf8_project.hello.HelloDto;
import de.szut.lf8_project.hello.HelloEntity;
import de.szut.lf8_project.hello.HelloService;
import de.szut.lf8_project.mapping.MappingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "hello")
public class HelloController {
    private final HelloService service;
    private final MappingService mappingService;

    public HelloController(HelloService service, MappingService mappingService) {
        this.service = service;
        this.mappingService = mappingService;
    }

    @Operation(summary = "creates a new hello with its id and message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created hello",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HelloDto.class))}),
            @ApiResponse(responseCode = "400", description = "invalid JSON posted",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "not authorized",
                    content = @Content)})
    @PostMapping
    public HelloDto create(@RequestBody @Valid HelloCreateDto helloCreateDto) {
        HelloEntity helloEntity = this.mappingService.mapHelloCreateDtotoEntity(helloCreateDto);
        helloEntity = this.service.create(helloEntity);
        return this.mappingService.mapHelloEntitytoDto(helloEntity);
    }

    @Operation(summary = "delivers a list of hellos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "list of hellos",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HelloDto.class))}),
            @ApiResponse(responseCode = "401", description = "not authorized",
                    content = @Content)})
    @GetMapping
    public List<HelloDto> findAll() {
        return this.service
                .readAll()
                .stream()
                .map(e -> this.mappingService.mapHelloEntitytoDto(e))
                .collect(Collectors.toList());
    }

    @Operation(summary = "deletes a qualification by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "delete successful"),
            @ApiResponse(responseCode = "401", description = "not authorized",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "resource not found",
                    content = @Content)})
    @DeleteMapping
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteHelloById(@PathVariable long id) {
        var entity = this.service.readById(id);
        if (entity != null) {
            throw new ResourceNotFoundException("HelloEntity not found on id = " + id);
        } else {
            this.service.delete(entity);
        }
    }

    @Operation(summary = "find hellos by message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of hellos who have the given message",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HelloDto.class))}),
            @ApiResponse(responseCode = "404", description = "qualification description does not exist",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "not authorized",
                    content = @Content)})
    @GetMapping("/findByMessage")
    public List<HelloDto> findAllEmployeesByQualification(@PathVariable String message) {
        return this.service
                .findByMessage(message)
                .stream()
                .map(e -> this.mappingService.mapHelloEntitytoDto(e))
                .collect(Collectors.toList());
    }
}
