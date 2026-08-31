package com.credit.bladi.datasource.documentsuggestion.mapper;

import com.credit.bladi.datasource.documentsuggestion.entity.DocumentSuggestionEntity;
import com.credit.bladi.model.documentsuggestion.DocumentSuggestionModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.DocumentSuggestionScope;
import com.credit.bladi.model.enums.EnterpriseType;

public final class DocumentSuggestionEntityMapper {
    private DocumentSuggestionEntityMapper() {
    }

    public static DocumentSuggestionModel mapFromEntity(DocumentSuggestionEntity entity) {
        return new DocumentSuggestionModel(entity.getId(), entity.getPublicId(), entity.getEnterpriseType(),
            entity.getCspCode(), entity.getLabel(), entity.getCreatedAt());
    }

    public static DocumentSuggestionEntity mapToNewEntity(DocumentSuggestionScope scope, EnterpriseType enterpriseType,
            CspCode cspCode, String label) {
        DocumentSuggestionEntity entity = new DocumentSuggestionEntity();
        entity.setClientScope(scope);
        entity.setEnterpriseType(enterpriseType);
        entity.setCspCode(cspCode);
        entity.setLabel(label);
        return entity;
    }
}
