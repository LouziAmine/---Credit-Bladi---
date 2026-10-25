package com.credit.bladi.controller.client;

import com.credit.bladi.controller.dto.workflow.response.DemandDocumentResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DocumentRequestResponseDTO;
import com.credit.bladi.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Client Document Requests", description = "CLIENT-facing: see which documents a Manager has "
    + "asked for on one of the portfolio's demands, and upload exactly what was asked: see "
    + "/api/v1/demands/{id}/document-requests for the Manager-facing side.")
public interface IClientDocumentRequestController {
    @Operation(
        summary = "List document requests (tags) on a demand in my portfolio",
        description = "403 unless the current CLIENT has an APPROVED link to this demand's Enterprise."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Requests (possibly empty)",
            content = @Content(schema = @Schema(implementation = DocumentRequestResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "No approved link to this demand's Enterprise",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "No demand found for the given ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<List<DocumentRequestResponseDTO>> list(
        @Parameter(description = "Identifier of the demand.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID demandId
    );

    @Operation(
        summary = "Fulfil a document request by uploading the corresponding file",
        description = "Goes through the exact same validation as a Manager's own upload (content-type "
            + "sniffing, size cap, antivirus scan); on success marks the tag FULFILLED and notifies the "
            + "Manager who asked for it (email + in-app, no SMS)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Document stored, request marked FULFILLED",
            content = @Content(schema = @Schema(implementation = DemandDocumentResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "No approved link to this demand's Enterprise",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "No demand/request found for the given IDs, or the "
            + "request does not belong to this demand",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "File is empty, exceeds the maximum size, has a "
            + "content type outside the whitelist, or was flagged by the antivirus scan",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DemandDocumentResponseDTO> uploadForRequest(
        @Parameter(description = "Identifier of the demand.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID demandId,

        @Parameter(description = "Identifier of the document request (tag) being fulfilled.",
            example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID requestId,

        @Parameter(description = "The file to upload.")
        @RequestParam("file") MultipartFile file
    );

    @Operation(
        summary = "Download the document that fulfilled one of my document requests",
        description = "Scoped to the document that fulfilled *this* request: a client can never fetch an "
            + "arbitrary Manager-uploaded document on the demand this way, only what they themselves provided "
            + "in answer to a specific tag."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "File content"),
        @ApiResponse(responseCode = "403", description = "No approved link to this demand's Enterprise",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "No demand/request found for the given IDs, or the "
            + "request does not belong to this demand",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "This request has not been fulfilled yet",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Resource> downloadFulfilledDocument(
        @Parameter(description = "Identifier of the demand.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID demandId,

        @Parameter(description = "Identifier of the document request (tag) whose document to download.",
            example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID requestId
    );
}
