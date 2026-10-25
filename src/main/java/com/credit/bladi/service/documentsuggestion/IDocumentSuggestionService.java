package com.credit.bladi.service.documentsuggestion;

import com.credit.bladi.model.documentsuggestion.DocumentSuggestionModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.EnterpriseType;
import java.util.List;
import java.util.UUID;

public interface IDocumentSuggestionService {
    DocumentSuggestionModel createDocumentSuggestion(EnterpriseType enterpriseType, CspCode cspCode, String label);

    List<DocumentSuggestionModel> findSuggestions(EnterpriseType enterpriseType, CspCode cspCode);

    Long resolveDocumentSuggestionId(UUID publicId);

    void deleteDocumentSuggestion(Long id);
}
