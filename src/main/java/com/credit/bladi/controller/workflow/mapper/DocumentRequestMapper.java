package com.credit.bladi.controller.workflow.mapper;

import com.credit.bladi.controller.dto.workflow.response.DocumentRequestResponseDTO;
import com.credit.bladi.model.workflow.DocumentRequestModel;

public final class DocumentRequestMapper {
    private DocumentRequestMapper() {
    }

    public static DocumentRequestResponseDTO toResponseDTO(DocumentRequestModel model) {
        return new DocumentRequestResponseDTO(
            model.publicId(),
            model.demandPublicId(),
            model.documentType(),
            model.status().name(),
            model.requestedByUsername(),
            model.createdAt(),
            model.fulfilledDocumentPublicId(),
            model.fulfilledAt());
    }
}
