package com.credit.bladi.controller.dto.workflow.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "AddDemandCommentRequestDTO", description = "Request payload to leave a remark on a demand's "
    + "current step, without advancing or (re)assigning it.")
public record AddDemandCommentRequestDTO(
    @Schema(
        description = "The remark.",
        example = "Waiting for the client's supporting document before proceeding.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.addDemandComment.comment.notBlank}")
    @Size(max = 1000, message = "{validation.addDemandComment.comment.size}")
    String comment
) {}
