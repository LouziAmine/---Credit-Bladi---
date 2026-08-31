package com.credit.bladi.controller.documentsuggestion.mapper;

import com.credit.bladi.controller.dto.documentsuggestion.response.DocumentSuggestionResponseDTO;
import com.credit.bladi.model.documentsuggestion.DocumentSuggestionModel;

public final class DocumentSuggestionControllerMapper {
    private DocumentSuggestionControllerMapper() {
    }

    public static DocumentSuggestionResponseDTO toResponseDTO(DocumentSuggestionModel model) {
        return new DocumentSuggestionResponseDTO(model.publicId(), model.enterpriseType(), model.cspCode(),
            model.label(), model.createdAt());
    }
}
