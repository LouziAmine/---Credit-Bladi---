package com.credit.bladi.controller.dto.workflow.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "DocumentRequestResponseDTO",
    description = "A document tag on a demand: what the Manager asked the client for, and whether it has "
        + "been provided yet.")
public record DocumentRequestResponseDTO(
    @Schema(description = "Request id.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") UUID id,
    @Schema(description = "Public id of the demand this request belongs to.",
        example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID demandId,
    @Schema(description = "Name of the document asked for.", example = "ID document") String documentType,
    @Schema(description = "PENDING or FULFILLED.", example = "PENDING") String status,
    @Schema(description = "Manager who asked for this document.", example = "chargee.affaires1")
        String requestedByUsername,
    @Schema(description = "When this request was created.") Instant createdAt,
    @Schema(description = "Public id of the document that fulfilled this request (null while PENDING).",
        example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID fulfilledDocumentId,
    @Schema(description = "When this request was fulfilled (null while PENDING).") Instant fulfilledAt
) {}
