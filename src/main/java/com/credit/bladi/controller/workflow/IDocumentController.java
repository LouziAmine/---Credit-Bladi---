package com.credit.bladi.controller.workflow;

import com.credit.bladi.controller.dto.workflow.response.DemandDocumentResponseDTO;
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

@Tag(name = "Demand Documents", description = "Generic, reusable document upload/download attached to a "
    + "demand's processing pipeline: one slot per step, kept as an immutable, append-only record for "
    + "banking-grade audit and retention.")
public interface IDocumentController {
    @Operation(
        summary = "Upload a document on a demand's current step",
        description = "Content type is detected from the file's actual bytes (never trusted from the "
            + "client-supplied header) and checked against the server's whitelist; the file is then "
            + "antivirus-scanned when scanning is enabled for this environment. If documentType is set and "
            + "an ACTIVE document of the same type already exists on this demand, it is marked SUPERSEDED "
            + "instead of being replaced or deleted: nothing is ever removed."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Document stored",
            content = @Content(schema = @Schema(implementation = DemandDocumentResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "No demand found for the given ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "File is empty, exceeds the maximum size, has a "
            + "content type outside the whitelist, or was flagged by the antivirus scan",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DemandDocumentResponseDTO> upload(
        @Parameter(description = "Identifier of the demand.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID id,

        @Parameter(description = "The file to upload.")
        @RequestParam("file") MultipartFile file,

        @Parameter(description = "Optional free-text tag for the kind of document (e.g. \"ID document\"). "
            + "Uploading a new document with the same tag supersedes the previous one.",
            example = "ID document")
        @RequestParam(name = "documentType", required = false) String documentType
    );

    @Operation(
        summary = "List every document uploaded on a demand",
        description = "Returns both ACTIVE and SUPERSEDED documents, oldest first: nothing is hidden, "
            + "matching this API's audit-trail conventions elsewhere."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Documents (possibly empty)",
            content = @Content(schema = @Schema(implementation = DemandDocumentResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "No demand found for the given ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<List<DemandDocumentResponseDTO>> list(
        @Parameter(description = "Identifier of the demand.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID id
    );

    @Operation(
        summary = "Download a previously uploaded document",
        description = "Blocked unless the document's antivirus scan verdict is CLEAN or SKIPPED."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "File content"),
        @ApiResponse(responseCode = "404", description = "No demand or document found for the given IDs",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Document has not completed its antivirus scan yet",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Resource> download(
        @Parameter(description = "Identifier of the demand.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID id,

        @Parameter(description = "Identifier of the document.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID documentId
    );
}
