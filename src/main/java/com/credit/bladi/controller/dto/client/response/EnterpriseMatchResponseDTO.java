package com.credit.bladi.controller.dto.client.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EnterpriseMatchResponseDTO", description = "Result of looking up an Enterprise by a "
    + "normalized comparison of a free-text company name (e.g. as typed into the public simulator).")
public record EnterpriseMatchResponseDTO(
    @Schema(description = "Whether an existing Enterprise was found for the given name.", example = "true")
    boolean matched,
    @Schema(description = "The matched Enterprise, null if matched is false.") EnterpriseResponseDTO enterprise
) {}
