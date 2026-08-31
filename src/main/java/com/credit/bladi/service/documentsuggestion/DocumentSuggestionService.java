package com.credit.bladi.service.documentsuggestion;

import static com.credit.bladi.exception.DocumentSuggestionException.DocumentSuggestionExceptionType.DUPLICATE_LABEL;
import static com.credit.bladi.exception.DocumentSuggestionException.DocumentSuggestionExceptionType.NOT_FOUND;
import static com.credit.bladi.exception.DocumentSuggestionException.DocumentSuggestionExceptionType.SCOPE_CONFLICT;

import com.credit.bladi.exception.DocumentSuggestionException;
import com.credit.bladi.model.documentsuggestion.DocumentSuggestionModel;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.DocumentSuggestionScope;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.repository.documentsuggestion.IDocumentSuggestionRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentSuggestionService implements IDocumentSuggestionService {
    private final IDocumentSuggestionRepository repository;

    private final IAuditService auditService;

    private final ICurrentActorProvider currentActorProvider;

    public DocumentSuggestionService(IDocumentSuggestionRepository repository, IAuditService auditService,
                                      ICurrentActorProvider currentActorProvider) {
        this.repository = repository;
        this.auditService = auditService;
        this.currentActorProvider = currentActorProvider;
    }

    @Override
    @Transactional
    public DocumentSuggestionModel createDocumentSuggestion(EnterpriseType enterpriseType, CspCode cspCode,
            String label) {
        if (enterpriseType != null && cspCode != null) {
            throw DocumentSuggestionException.of(SCOPE_CONFLICT);
        }
        boolean duplicate = cspCode != null
            ? repository.existsByCspCodeAndLabelIgnoreCase(cspCode, label)
            : repository.existsByEnterpriseTypeAndLabelIgnoreCase(enterpriseType, label);
        if (duplicate) {
            throw DocumentSuggestionException.of(DUPLICATE_LABEL);
        }
        DocumentSuggestionScope scope = cspCode != null ? DocumentSuggestionScope.CSP
            : DocumentSuggestionScope.ENTERPRISE;
        DocumentSuggestionModel created = repository.create(scope, enterpriseType, cspCode, label);
        auditService.log(currentActorProvider.getCurrentUsername(), AuditAction.DOCUMENT_SUGGESTION_CREATE,
            "documentSuggestionId=" + created.id() + ", enterpriseType=" + enterpriseType + ", cspCode=" + cspCode
                + ", label=" + created.label());
        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentSuggestionModel> findSuggestions(EnterpriseType enterpriseType, CspCode cspCode) {
        if (cspCode != null) {
            return repository.findApplicableToCsp(cspCode);
        }
        if (enterpriseType != null) {
            return repository.findApplicableTo(enterpriseType);
        }
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Long resolveDocumentSuggestionId(UUID publicId) {
        return repository.findIdByPublicId(publicId)
            .orElseThrow(() -> DocumentSuggestionException.of(NOT_FOUND));
    }

    @Override
    @Transactional
    public void deleteDocumentSuggestion(Long id) {
        if (!repository.existsById(id)) {
            throw DocumentSuggestionException.of(NOT_FOUND);
        }
        repository.deleteById(id);
        auditService.log(currentActorProvider.getCurrentUsername(), AuditAction.DOCUMENT_SUGGESTION_DELETE,
            "documentSuggestionId=" + id);
    }
}
