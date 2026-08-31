package com.credit.bladi.controller.dto.servicing.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "DeclarePaymentRequestDTO", description = "Request payload for a client declaring having paid "
    + "an installment (\"I paid\"): carries the RIB they say they paid from, so the manager "
    + "managing the file can check it against the actual bank statement before validating.")
public record DeclarePaymentRequestDTO(
    @Schema(
        description = "RIB of the account the client paid from.",
        example = "230 780 0123456789012345 95",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.declarePayment.sourceRib.notBlank}")
    @Size(min = 10, max = 34, message = "{validation.declarePayment.sourceRib.size}")
    String sourceRib
) {}
