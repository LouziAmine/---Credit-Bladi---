package com.credit.bladi.controller.documentsuggestion;

import com.credit.bladi.controller.dto.documentsuggestion.request.CreateDocumentSuggestionRequestDTO;
import com.credit.bladi.controller.dto.documentsuggestion.response.DocumentSuggestionResponseDTO;
import com.credit.bladi.exception.ErrorResponse;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.EnterpriseType;
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
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Document Suggestions", description = "Manager-editable list of document-name quick-add tags shown "
    + "when requesting documents from a client, scoped per enterprise type or per CSP profile (or common to "
    + "all values in that dimension).")
public interface IDocumentSuggestionController {
    @Operation(summary = "Add a new document suggestion (manager use only)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Document suggestion created",
            content = @Content(schema = @Schema(implementation = DocumentSuggestionResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request payload",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "A suggestion with this label already exists for this "
            + "enterprise type/CSP profile",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Both enterpriseType and cspCode were set: they are "
            + "mutually exclusive", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DocumentSuggestionResponseDTO> createDocumentSuggestion(
        @Valid @RequestBody CreateDocumentSuggestionRequestDTO requestDTO);

    @Operation(summary = "List document suggestions. Filtered to one CSP profile (plus the common ones) when "
        + "cspCode is given, to one enterprise type (plus the common ones) when enterpriseType is given, or every "
        + "suggestion otherwise. cspCode takes precedence if both are given.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Matching document suggestions",
            content = @Content(schema = @Schema(implementation = DocumentSuggestionResponseDTO.class)))
    })
    ResponseEntity<List<DocumentSuggestionResponseDTO>> getDocumentSuggestions(
        @Parameter(description = "Restrict to suggestions applicable to this enterprise type (plus common ones).")
        EnterpriseType enterpriseType,
        @Parameter(description = "Restrict to suggestions applicable to this CSP profile (plus common ones).")
        CspCode cspCode);

    @Operation(summary = "Delete a document suggestion (manager use only)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Document suggestion deleted"),
        @ApiResponse(responseCode = "404", description = "Document suggestion not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> deleteDocumentSuggestion(UUID id);
}
