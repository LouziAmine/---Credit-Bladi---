package com.credit.bladi.controller.auth;

import com.credit.bladi.controller.dto.auth.request.LoginRequestDTO;
import com.credit.bladi.controller.dto.auth.request.RegisterRequestDTO;
import com.credit.bladi.controller.dto.auth.response.AuthUserResponseDTO;
import com.credit.bladi.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Authentication", description = "Login, registration and session endpoints")
public interface IAuthController {
    @Operation(
        summary = "Register a new CLIENT account",
        description = "Creates a new user with the CLIENT role. Username must be unique."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Client registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Username already taken",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    { "errorCode": "CB-409", "description": "Username is already taken." }
                    """)))
    })
    ResponseEntity<Void> register(@Valid @RequestBody RegisterRequestDTO dto);

    @Operation(
        summary = "Login",
        description = "Authenticates with username and password. "
            + "Sets a signed JWT in an HttpOnly cookie (cb_access_token). "
            + "Sets a refresh token in an HttpOnly cookie (cb_refresh_token, 7 days). "
            + "Returns authenticated user info { username, role }."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(schema = @Schema(implementation = AuthUserResponseDTO.class),
                examples = @ExampleObject(value = """
                    { "username": "john.doe", "role": "CLIENT" }
                    """))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    { "errorCode": "CB-401", "description": "Invalid username or password." }
                    """))),
        @ApiResponse(responseCode = "423", description = "Account temporarily locked after too many failed "
            + "login attempts",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    { "errorCode": "CB-423", "description": "Account temporarily locked due to too many \
                    failed login attempts. Please try again in 15 minutes." }
                    """)))
    })
    ResponseEntity<AuthUserResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto,
                                              HttpServletRequest request,
                                              HttpServletResponse response);

    @Operation(
        summary = "Refresh access token",
        description = "Issues a new access token using the cb_refresh_token cookie. "
            + "Rotates the refresh token on each call (old token is revoked). "
            + "Returns new cookies and user info."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "New access token issued",
            content = @Content(schema = @Schema(implementation = AuthUserResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<AuthUserResponseDTO> refresh(HttpServletRequest request, HttpServletResponse response);

    @Operation(
        summary = "Logout: revokes the current JWT and refresh token",
        description = "Clears both HttpOnly cookies and blacklists the access token + revokes the refresh token."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Logged out successfully: this endpoint is "
            + "intentionally public (permitAll) so an expired access token doesn't block refresh-token "
            + "revocation; both tokens are null-checked internally.")
    })
    ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response);

    @Operation(
        summary = "Get current user info",
        description = "Returns { username, role } of the authenticated user."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Authenticated user info",
            content = @Content(schema = @Schema(implementation = AuthUserResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<AuthUserResponseDTO> me();
}
