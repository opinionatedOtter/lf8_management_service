package de.szut.lf8_project.domain.adapter.openApi;

import de.szut.lf8_project.controller.dtos.EmployeeProjectViewWrapper;
import de.szut.lf8_project.controller.dtos.ProjectView;
import de.szut.lf8_project.controller.dtos.commands.AddEmployeeCommand;
import de.szut.lf8_project.controller.dtos.commands.CreateProjectCommand;
import de.szut.lf8_project.controller.dtos.commands.UpdateProjectCommand;
import de.szut.lf8_project.domain.adapter.openApi.schemas.AddEmployeeCommandSchema;
import de.szut.lf8_project.domain.adapter.openApi.schemas.CreateProjectCommandSchema;
import de.szut.lf8_project.domain.adapter.openApi.schemas.EmployeeProjectViewSchema;
import de.szut.lf8_project.domain.adapter.openApi.schemas.ProblemDetailsSchema;
import de.szut.lf8_project.domain.adapter.openApi.schemas.ProjectViewSchema;
import de.szut.lf8_project.domain.adapter.openApi.schemas.UpdateProjectCommandSchema;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@Tag(name = "Project Methods")
public interface OpenApiProjectController {

    @Operation(summary = "Create a new project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Your project was successfully created",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectViewSchema.class))}
            ),
            @ApiResponse(responseCode = "400",
                    description = "Malformed request or invalid parameter",
                    content = {@Content(schema = @Schema(implementation = ProblemDetailsSchema.class))}
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
                    content = {@Content(schema = @Schema(implementation = ProblemDetailsSchema.class))}
            ),
            @ApiResponse(responseCode = "503",
                    description = "The service is currently unavailable",
                    content = {@Content(schema = @Schema(implementation = ProblemDetailsSchema.class))}
            )
    })
    ResponseEntity<ProjectView> createProject(
            @Valid @RequestBody @Schema(implementation = CreateProjectCommandSchema.class) CreateProjectCommand createProjectCommand,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader
    );


    @Operation(summary = "Updates an existing project. Accepts a force-flag to remove all team members who are unavailable in the new project duration.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "The project was successfully updated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectViewSchema.class))}
            ),
            @ApiResponse(responseCode = "400",
                    description = "Malformed request or invalid parameter",
                    content = {@Content(schema = @Schema(implementation = ProblemDetailsSchema.class))}
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
                    content = {@Content(schema = @Schema(implementation = ProblemDetailsSchema.class))}
            )
    })
    ResponseEntity<ProjectView> updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody @Schema(implementation = UpdateProjectCommandSchema.class) UpdateProjectCommand updateProjectCommand,
            @RequestParam (required = false) boolean isForced,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader
    );

    @Operation(summary = "Get a specfic project via it's ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "The project was successfully returned",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectViewSchema.class))}
            ),
            @ApiResponse(responseCode = "400",
                    description = "Malformed request or invalid parameter",
                    content = {@Content(schema = @Schema(implementation = ProblemDetailsSchema.class))}
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
                    content = {@Content(schema = @Schema(implementation = ProblemDetailsSchema.class))}
            ),
            @ApiResponse(responseCode = "503",
                    description = "The service is currently unavailable",
                    content = {@Content(schema = @Schema(implementation = ProblemDetailsSchema.class))}
            )
    })
    ResponseEntity<ProjectView> getProjectById(
            @Valid @PathVariable Long id
    );

    @Operation(summary = "Delete a project by it's ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "The project was successfully deleted",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectViewSchema.class))}
            ),
            @ApiResponse(responseCode = "400",
                    description = "Malformed request or invalid parameter",
                    content = {@Content(schema = @Schema(implementation = ProblemDetailsSchema.class))}
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
                    content = {@Content(schema = @Schema(implementation = ProblemDetailsSchema.class))}
            ),
            @ApiResponse(responseCode = "500",
                    description = "An unknown error occurred, please try again later",
                    content = {@Content(schema = @Schema(implementation = ProblemDetailsSchema.class))}
            )
    })
    ResponseEntity<Void> deleteProject(
            @Valid @PathVariable Long id
    );

    @Operation(summary = "Get a list of all Projects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "The list of all Projects was returned",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectViewSchema.class))}
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
                    content = {@Content(schema = @Schema(implementation = ProblemDetailsSchema.class))}
            )
    })
    ResponseEntity<List<ProjectView>> getAllProjects();

    @Operation(summary = "Add an Employee to an existing Project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Employee was added, Project is returned",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectViewSchema.class))}
            ),
            @ApiResponse(responseCode = "400",
                    description = "Malformed request, invalid parameter, employee has wrong role, employee already planned in other project",
                    content = {@Content(schema = @Schema(implementation = ProblemDetailsSchema.class))}
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
                    content = {@Content(schema = @Schema(implementation = ProblemDetailsSchema.class))}
            ),
            @ApiResponse(responseCode = "415",
                    description = "Invalid content type",
                    content = {@Content(schema = @Schema(hidden = true))}
            ),
            @ApiResponse(responseCode = "500",
                    description = "An unknown error occurred, please try again later",
                    content = {@Content(schema = @Schema(implementation = ProblemDetailsSchema.class))}
            )
    })
    @PostMapping("/{projectId}")
    ResponseEntity<ProjectView> addEmployee(
            @Valid @PathVariable Long projectId,
            @Valid @RequestBody @Schema(implementation = AddEmployeeCommandSchema.class) AddEmployeeCommand addEmployeeCommand,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader
    );

    @Operation(summary = "Get all projects from an employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Projects of the employee were successfully returned",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeProjectViewSchema.class))}
            ),
            @ApiResponse(responseCode = "400",
                    description = "Malformed request or invalid parameter",
                    content = {@Content(schema = @Schema(implementation = ProblemDetailsSchema.class))}
            ),
            @ApiResponse(responseCode = "401",
                    description = "Please provide a valid bearer token",
                    content = {@Content(schema = @Schema(hidden = true))}
            ),
            @ApiResponse(responseCode = "403",
                    description = "You do not have the required user permissions for this action",
                    content = {@Content(schema = @Schema(hidden = true))}
            ),
            @ApiResponse(responseCode = "500",
                    description = "An unknown error occurred, please try again later",
                    content = {@Content(schema = @Schema(implementation = ProblemDetailsSchema.class))}
            )
    })
    @GetMapping("/byEmployee/{employeeId}")
    ResponseEntity<EmployeeProjectViewWrapper> getAllProjectsOfEmployee(
            @Valid @PathVariable Long employeeId,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader
    );

    @Operation(summary = "Remove an employee from an existing project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Employee was removed",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectViewSchema.class))}
            ),
            @ApiResponse(responseCode = "400",
                    description = "Malformed request and/or invalid parameter",
                    content = {@Content(schema = @Schema(implementation = ProblemDetailsSchema.class))}
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
                    description = "No project or employee in project found",
                    content = {@Content(schema = @Schema(implementation = ProblemDetailsSchema.class))}
            ),
            @ApiResponse(responseCode = "415",
                    description = "Invalid content type",
                    content = {@Content(schema = @Schema(hidden = true))}
            ),
            @ApiResponse(responseCode = "500",
                    description = "An unknown error occurred, please try again later",
                    content = {@Content(schema = @Schema(implementation = ProblemDetailsSchema.class))}
            )
    })
    @DeleteMapping("/{id}/removeEmployee/{employeeId}")
    ResponseEntity<Void> removeEmployeeFromProject(
            @Valid @PathVariable Long id,
            @Valid @PathVariable Long employeeId
    );
}