package de.szut.lf8_project.domain.adapter;

import de.szut.lf8_project.common.JWT;
import de.szut.lf8_project.controller.ProblemDetails.ProblemDetails;
import de.szut.lf8_project.controller.dtos.AddEmployeeCommand;
import de.szut.lf8_project.controller.dtos.CreateProjectCommand;
import de.szut.lf8_project.controller.dtos.ProjectView;
import de.szut.lf8_project.controller.dtos.UpdateProjectCommand;
import de.szut.lf8_project.domain.project.ProjectId;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@Tag(name="Project Methods")
public interface OpenApiProjectController {

    @Operation(summary = "Create a new project")
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
            @ApiResponse(responseCode = "415",
                    description = "Invalid content type",
                    content = {@Content(schema = @Schema(hidden = true))}
            ),
            @ApiResponse(responseCode = "500",
                    description = "An unknown error occurred, please try again later",
                    content = {@Content(schema = @Schema(implementation = ProblemDetails.class))}
            ),
            @ApiResponse(responseCode = "503",
                    description = "The service is currently unavailable",
                    content = {@Content(schema = @Schema(implementation = ProblemDetails.class))}
            )
    })
    ResponseEntity<ProjectView> createProject(
            @Valid @RequestBody CreateProjectCommand createProjectCommand,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader
    );


    @Operation(summary = "Updates an existing project. Accepts a force-flag to remove all team members who are unavailable in the new project duration.")
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
            @RequestParam (required = false) boolean isForced,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader
    );

    @Operation(summary = "Get a specfic project via it's ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "The project was successfully returned",
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
            @ApiResponse(responseCode = "415",
                    description = "Invalid content type",
                    content = {@Content(schema = @Schema(hidden = true))}
            ),
            @ApiResponse(responseCode = "500",
                    description = "An unknown error occurred, please try again later",
                    content = {@Content(schema = @Schema(implementation = ProblemDetails.class))}
            ),
            @ApiResponse(responseCode = "503",
                    description = "The service is currently unavailable",
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
            @ApiResponse(responseCode = "500",
                    description = "An unknown error occurred, please try again later",
                    content = {@Content(schema = @Schema(implementation = ProblemDetails.class))}
            )
    })
    ResponseEntity<List<ProjectView>> getAllProjects();

    @Operation(summary = "Add an Employee to an existing Project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Employee was added, Project is returned",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectView.class))}
            ),
            @ApiResponse(responseCode = "400",
                    description = "Malformed request, invalid parameter, employee has wrong role, employee already planned in other project",
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
                    description = "No project found for given ID or no employee found for given ID",
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
    @PostMapping("/{projectId}")
    public ResponseEntity<ProjectView> addEmployee(
            @Valid @PathVariable Long projectId,
            @Valid @RequestBody AddEmployeeCommand addEmployeeCommand,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader
    );
}