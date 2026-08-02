package com.credit.bladi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "ErrorResponse", description = "Error response returned by the API")
public record ErrorResponse(

    @Schema(description = "Unique request identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    String requestId,

    @Schema(description = "Error timestamp", example = "2026-06-07 14:30:00")
    String timestamp,

    @Schema(description = "Error code", example = "CB-400")
    String errorCode,

    @Schema(description = "Error description", example = "Field 'amount' is invalid or missing.")
    String description
) {

    @Override
    public String toString() {
        return String.format("ErrorResponse [requestId=%s, timestamp=%s, errorCode=%s, description=%s]",
            requestId, timestamp, errorCode, description);
    }
}