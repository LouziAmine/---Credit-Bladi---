package com.credit.bladi.controller.user;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.user.request.CreateUserRequestDTO;
import com.credit.bladi.controller.dto.user.request.UpdateUserRoleRequestDTO;
import com.credit.bladi.controller.dto.user.response.UserResponseDTO;
import com.credit.bladi.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "User Management", description = "Back-office user management — requires MANAGER role")
public interface IUserController {

    @Operation(
        summary = "List all users (paginated)",
        description = "Requires a valid MANAGER JWT token."
    )

    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paginated list of users",
            content = @Content(schema = @Schema(implementation = CreditPage.class))),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions — MANAGER role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<CreditPage<UserResponseDTO>> getAllUsers(
        @Parameter(description = "Page number (1-based)", name = "page", example = "1")
        @Min(value = 1, message = "INVALID_PAGE: page must be greater or equal to 1.")
        @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,

        @Parameter(description = "Page size (max 50)", name = "size", example = "20")
        @Min(value = 1, message = "INVALID_SIZE: size must be between 1 and 50.")
        @Max(value = 50, message = "INVALID_SIZE: size must be between 1 and 50.")
        @RequestParam(name = "size", defaultValue = "20", required = false) Integer size
    );

    @Operation(
        summary = "Create a user with any role",
        description = "Requires a valid MANAGER JWT token."
    )

    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions — MANAGER role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Username already taken",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    { "errorCode": "CB-409", "description": "Username is already taken." }
                    """)))
    })
    ResponseEntity<Void> createUser(@Valid @RequestBody CreateUserRequestDTO dto);

    @Operation(
        summary = "Get a user by id",
        description = "Requires a valid MANAGER JWT token."
    )

    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "400", description = "Invalid id",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions — MANAGER role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    { "errorCode": "CB-404", "description": "User not found." }
                    """)))
    })
    ResponseEntity<UserResponseDTO> getUserById(
        @Min(value = 1, message = "INVALID_ID: id must be a positive number.")
        @PathVariable Long id);

    @Operation(
        summary = "Update a user's role",
        description = "Requires a valid MANAGER JWT token."
    )

    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Role updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions — MANAGER role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    { "errorCode": "CB-404", "description": "User not found." }
                    """)))
    })
    ResponseEntity<Void> updateUserRole(
        @Min(value = 1, message = "INVALID_ID: id must be a positive number.")
        @PathVariable Long id,
        @Valid @RequestBody UpdateUserRoleRequestDTO dto,
        @AuthenticationPrincipal UserDetails currentUser);
}