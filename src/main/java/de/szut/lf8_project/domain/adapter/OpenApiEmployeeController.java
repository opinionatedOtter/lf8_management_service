package de.szut.lf8_project.domain.adapter;

import de.szut.lf8_project.controller.ProblemDetails.ProblemDetails;
import de.szut.lf8_project.controller.dtos.EmployeesOfProject;
import de.szut.lf8_project.controller.dtos.ProjectView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.Valid;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@Tag(name="Employee Methods")
public interface OpenApiEmployeeController {

    @Operation(summary = "List all employees of a project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "List of employees is returned",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmployeesOfProject.class))}
            ),
            @ApiResponse(responseCode = "400",
                    description = "Malformed request or invalid parameter",
                    content = {@Content(schema = @Schema(implementation = ProblemDetails.class))}
            ),
            @ApiResponse(responseCode = "401",
                    description = "Please provide a valid bearer token",
                    content = {@Content(schema = @Schema(hidden = true))}
            ),
            @ApiResponse(responseCode = "403",
                    description = "You do not have the required user permissions for this action.",
                    content = {@Content(schema = @Schema(hidden = true))}
            ),
            @ApiResponse(responseCode = "404",
                    description = "No project found for given ID",
                    content = {@Content(schema = @Schema(implementation = ProblemDetails.class))}
            ),
            @ApiResponse(responseCode = "415",
                    description = "Invalid content type",
                    content = {@Content(schema = @Schema(hidden = true))}
            ),
            @ApiResponse(responseCode = "500",
                    description = "An unknown error occurred, please try again later",
                    content = {@Content(schema = @Schema(implementation = ProblemDetails.class))}
            )
    })
    @GetMapping("/byProject/{id}")
    ResponseEntity<EmployeesOfProject> getEmployeesByProject(
            @Valid @PathVariable Long id
    );
}
