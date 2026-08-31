package com.credit.bladi.controller.audit;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.audit.response.AuditLogResponseDTO;
import com.credit.bladi.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Audit Log", description = "Back-office APIs for consulting the compliance audit trail")
public interface IAuditLogController {
    @Operation(
        summary = "Returns audit log entries (paginated)",
        description = "Allows the bank administrator to browse the compliance audit trail "
            + "(logins, simulations, BAM rejections, parameter/role changes), most recent first."
    )

    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Paginated list of audit log entries",
            content = @Content(schema = @Schema(implementation = CreditPage.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid pagination parameters",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-07-08 10:00:00",
                      "errorCode": "CB-400",
                      "description": "INVALID_PAGE: page must be greater or equal to 1."
                    }
                    """))),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions: MANAGER role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-07-08 10:00:00",
                      "errorCode": "CB-500",
                      "description": "Internal server error, please try again later."
                    }
                    """)))
    })
    ResponseEntity<CreditPage<AuditLogResponseDTO>> getAuditLog(

        @Parameter(description = "Page number (1-based)", name = "page", example = "1")
        @Min(value = 1, message = "{validation.invalidPage}")
        @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,

        @Parameter(description = "Page size (max 50)", name = "size", example = "20")
        @Min(value = 1, message = "{validation.invalidSize}")
        @Max(value = 50, message = "{validation.invalidSize}")
        @RequestParam(name = "size", defaultValue = "20", required = false) Integer size,

        @Parameter(description = "Only entries about this kind of object (DEMAND, ENTERPRISE, USER); requires "
            + "entityId.", name = "entityType", example = "DEMAND")
        @RequestParam(name = "entityType", required = false) String entityType,

        @Parameter(description = "Public id of the object whose full audit trail is wanted; requires entityType.",
            name = "entityId", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @RequestParam(name = "entityId", required = false) UUID entityId
    );
}
