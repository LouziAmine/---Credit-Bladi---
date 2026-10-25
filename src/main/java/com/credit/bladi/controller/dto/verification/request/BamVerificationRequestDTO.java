package com.credit.bladi.controller.dto.verification.request;

import com.credit.bladi.validator.ValidationConstraints;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(
    name = "BamVerificationRequestDTO",
    description = "One company to run a simulated SCIP (unpaid cheques) + Centrale des Risques (outstanding "
        + "credit exposure) check for."
)
public record BamVerificationRequestDTO(
    @Schema(description = "Enterprise category. Must be one of: TPE, AUTO_ENTREPRENEUR, PME, ETI, GE.",
        example = "PME", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{validation.bamVerification.enterpriseType.notBlank}")
    @Pattern(regexp = ValidationConstraints.ENTERPRISE_TYPE_REGEX,
        message = "{validation.bamVerification.enterpriseType.pattern}")
    String enterpriseType,

    @Schema(description = "ICE (or CIN for an Auto-Entrepreneur's personal identity).",
        example = "001234567000089", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{validation.bamVerification.identifier.notBlank}")
    @Size(max = 50, message = "{validation.bamVerification.identifier.size}")
    String identifier,

    @Schema(description = "Company name, as declared by the applicant.", example = "Atlas Textile SARL",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{validation.bamVerification.companyName.notBlank}")
    @Size(max = 200, message = "{validation.bamVerification.companyName.size}")
    String companyName
) {}
