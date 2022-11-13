package de.szut.lf8_project.domain.adapter;

import de.szut.lf8_project.controller.ProblemDetails.ProblemDetails;
import de.szut.lf8_project.controller.dtos.CreateProjectCommand;
import de.szut.lf8_project.controller.dtos.ProjectView;
import de.szut.lf8_project.controller.dtos.UpdateProjectCommand;
import de.szut.lf8_project.domain.project.ProjectId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.validation.Valid;
import java.util.List;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public interface OpenApiProjectController {

    @Operation(summary = "Create a new Project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Your project was successfully created",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectView.class))}
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
            @ApiResponse(responseCode = "503",
                    description = "The service is currently unavailable",
                    content = {@Content(schema = @Schema(implementation = ProblemDetails.class))}
            ),
            @ApiResponse(responseCode = "500",
                    description = "An unknown error occurred, please try again later",
                    content = {@Content(schema = @Schema(implementation = ProblemDetails.class))}
            )
    })
    ResponseEntity<ProjectView> createProject(
            @Valid @RequestBody CreateProjectCommand createProjectCommand,
            @RequestHeader("Authorization") String authHeader
    );


    @Operation(summary = "Updates a existing project. Accepts a force-flag to remove all team members which are unavailable in the new project duration.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "The project was successfully updated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectView.class))}
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
            @ApiResponse(responseCode = "500",
                    description = "An unknown error occurred, please try again later",
                    content = {@Content(schema = @Schema(implementation = ProblemDetails.class))}
            )
    })
    ResponseEntity<ProjectView> updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody UpdateProjectCommand updateProjectCommand,
            @PathVariable(required = false) boolean forceFlag,
            @RequestHeader("Authorization") String authHeader
    );

    @Operation(summary = "Get a Project via it's ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "The Project was successfully returned",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectView.class))}
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
            @ApiResponse(responseCode = "503",
                    description = "The service is currently unavailable",
                    content = {@Content(schema = @Schema(implementation = ProblemDetails.class))}
            ),
            @ApiResponse(responseCode = "500",
                    description = "An unknown error occurred, please try again later",
                    content = {@Content(schema = @Schema(implementation = ProblemDetails.class))}
            )
    })
    ResponseEntity<ProjectView> getProjectById(
            @Valid @PathVariable Long id
    );

    @Operation(summary = "Get a list of all Projects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "The list of all Projects was returned",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectView.class))}
            ),
            @ApiResponse(responseCode = "401",
                    description = "Please provide a valid bearer token",
                    content = {@Content(schema = @Schema(hidden = true))}
            ),
            @ApiResponse(responseCode = "403",
                    description = "You do not have the required user permissions for this action.",
                    content = {@Content(schema = @Schema(hidden = true))}
            ),
            @ApiResponse(responseCode = "503",
                    description = "The service is currently unavailable",
                    content = {@Content(schema = @Schema(implementation = ProblemDetails.class))}
            ),
            @ApiResponse(responseCode = "500",
                    description = "An unknown error occurred, please try again later",
                    content = {@Content(schema = @Schema(implementation = ProblemDetails.class))}
            )
    })
    ResponseEntity<List<ProjectView>> getAllProjects();
}