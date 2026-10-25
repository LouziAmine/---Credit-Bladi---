package com.credit.bladi.controller.dto.workflow.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
    name = "CreateDocumentRequestRequestDTO",
    description = "A Manager asks the client for a specific piece of paperwork by name (e.g. \"ID "
        + "document\", \"RIB\"): shown as a tag on the client's view of this demand, letting them upload "
        + "exactly what was asked for instead of guessing."
)
public record CreateDocumentRequestRequestDTO(
    @Schema(description = "Name of the document required from the client.", example = "ID document",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{validation.createDocumentRequest.documentType.notBlank}")
    @Size(max = 50, message = "{validation.createDocumentRequest.documentType.size}")
    String documentType
) {}
