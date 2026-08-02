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
        @ApiResponse(responseCode = "403", description = "Insufficient permissions — MANAGER role required",
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
        @Min(value = 1, message = "INVALID_PAGE: page must be greater or equal to 1.")
        @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,

        @Parameter(description = "Page size (max 50)", name = "size", example = "20")
        @Min(value = 1, message = "INVALID_SIZE: size must be between 1 and 50.")
        @Max(value = 50, message = "INVALID_SIZE: size must be between 1 and 50.")
        @RequestParam(name = "size", defaultValue = "20", required = false) Integer size
    );
}
