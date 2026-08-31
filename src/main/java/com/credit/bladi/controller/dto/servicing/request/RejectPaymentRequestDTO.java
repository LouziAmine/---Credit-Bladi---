package com.credit.bladi.controller.dto.servicing.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "RejectPaymentRequestDTO", description = "Request payload for a manager rejecting a "
    + "client's declared payment: the reason is sent back to the client.")
public record RejectPaymentRequestDTO(
    @Schema(
        description = "Why the declared payment could not be validated.",
        example = "The declared RIB does not match any payment received in our account.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.rejectPayment.reason.notBlank}")
    @Size(max = 500, message = "{validation.rejectPayment.reason.size}")
    String reason
) {}
