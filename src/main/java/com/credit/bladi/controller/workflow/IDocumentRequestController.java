package com.credit.bladi.controller.workflow;

import com.credit.bladi.controller.dto.workflow.request.CreateDocumentRequestRequestDTO;
import com.credit.bladi.controller.dto.workflow.response.DocumentRequestResponseDTO;
import com.credit.bladi.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Document Requests", description = "Manager-facing: ask a client for a specific named document "
    + "on a demand: shown to the client as a tag they can fulfil directly, see /api/v1/me/demands/{id}/"
    + "document-requests for the client-facing side.")
public interface IDocumentRequestController {
    @Operation(
        summary = "Ask the client for a specific document",
        description = "Creates a PENDING tag visible on the client's view of this demand, and notifies the "
            + "client (email + SMS + in-app if their account is already linked) that a document is required."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Request created",
            content = @Content(schema = @Schema(implementation = DocumentRequestResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "No demand found for the given ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DocumentRequestResponseDTO> createRequest(
        @Parameter(description = "Identifier of the demand.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID id,

        @Valid @RequestBody CreateDocumentRequestRequestDTO requestDTO
    );

    @Operation(
        summary = "List every document request (tag) on a demand",
        description = "Both PENDING and FULFILLED, oldest first."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Requests (possibly empty)",
            content = @Content(schema = @Schema(implementation = DocumentRequestResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "No demand found for the given ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<List<DocumentRequestResponseDTO>> list(
        @Parameter(description = "Identifier of the demand.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID id
    );
}
