package com.credit.bladi.repository.documentsuggestion;

import com.credit.bladi.model.documentsuggestion.DocumentSuggestionModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.DocumentSuggestionScope;
import com.credit.bladi.model.enums.EnterpriseType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IDocumentSuggestionRepository {
    DocumentSuggestionModel create(DocumentSuggestionScope scope, EnterpriseType enterpriseType, CspCode cspCode,
            String label);

    Optional<Long> findIdByPublicId(UUID publicId);

    List<DocumentSuggestionModel> findApplicableTo(EnterpriseType enterpriseType);

    List<DocumentSuggestionModel> findApplicableToCsp(CspCode cspCode);

    List<DocumentSuggestionModel> findAll();

    boolean existsByEnterpriseTypeAndLabelIgnoreCase(EnterpriseType enterpriseType, String label);

    boolean existsByCspCodeAndLabelIgnoreCase(CspCode cspCode, String label);

    boolean existsById(Long id);

    void deleteById(Long id);
}
