package com.credit.bladi.controller.dto.documentsuggestion.response;

import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.EnterpriseType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "DocumentSuggestionResponseDTO", description = "A document-name suggestion usable as a quick-add "
    + "tag when requesting documents from a client.")
public record DocumentSuggestionResponseDTO(
    @Schema(description = "Suggestion id.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") UUID id,
    @Schema(description = "Enterprise type this suggestion applies to, null if common to every type "
        + "(or if this is a CSP-scoped suggestion).", example = "PME") EnterpriseType enterpriseType,
    @Schema(description = "CSP profile this suggestion applies to, null if common to every profile "
        + "(or if this is an enterprise-scoped suggestion).", example = "FONC") CspCode cspCode,
    @Schema(description = "Document name shown on the tag.", example = "Extrait du Registre de Commerce")
    String label,
    @Schema(description = "When this suggestion was added.") Instant createdAt
) {}
