package com.credit.bladi.controller.dto.documentsuggestion.request;

import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.EnterpriseType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
    name = "CreateDocumentSuggestionRequestDTO",
    description = """
        Request payload to add a document-name suggestion, shown as a quick-add tag when a manager
        requests documents from a client. Manager use only.

        enterpriseType and cspCode are mutually exclusive: at most one may be set. Setting cspCode
        scopes the suggestion to that CSP profile (mortgage clients); setting enterpriseType scopes it
        to that enterprise type; leaving both null scopes it to every enterprise type (the historical
        default).
        """
)
public record CreateDocumentSuggestionRequestDTO(
    @Schema(description = "Enterprise type this suggestion applies to. Null applies it to every type. "
        + "Mutually exclusive with cspCode.", example = "PME")
    EnterpriseType enterpriseType,

    @Schema(description = "CSP profile this suggestion applies to. Null applies it to every CSP profile. "
        + "Mutually exclusive with enterpriseType.", example = "FONC")
    CspCode cspCode,

    @Schema(description = "Document name shown on the tag.", example = "Extrait du Registre de Commerce",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{validation.createDocumentSuggestion.label.notBlank}")
    @Size(max = 200, message = "{validation.createDocumentSuggestion.label.size}")
    String label
) {}
