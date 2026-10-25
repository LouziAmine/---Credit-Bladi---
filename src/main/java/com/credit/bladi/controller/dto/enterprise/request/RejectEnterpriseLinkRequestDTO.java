package com.credit.bladi.controller.dto.enterprise.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(name = "RejectEnterpriseLinkRequestDTO", description = "Manager rejects a PENDING enterprise-user link.")
public record RejectEnterpriseLinkRequestDTO(
    @Schema(description = "Optional free-text reason, shown to the client.",
        example = "Registration number does not match any open file.")
    @Size(max = 500, message = "{validation.rejectEnterpriseLink.reason.size}")
    String reason
) {}
