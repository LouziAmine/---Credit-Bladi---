package com.credit.bladi.unittest.service.documentsuggestion;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.exception.DocumentSuggestionException;
import com.credit.bladi.model.documentsuggestion.DocumentSuggestionModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.DocumentSuggestionScope;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.repository.documentsuggestion.IDocumentSuggestionRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.documentsuggestion.DocumentSuggestionService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentSuggestionServiceTest {
    @Mock
    private IDocumentSuggestionRepository repository;

    @Mock
    private IAuditService auditService;

    @Mock
    private ICurrentActorProvider currentActorProvider;

    private DocumentSuggestionService service;

    @BeforeEach
    void setUp() {
        service = new DocumentSuggestionService(repository, auditService, currentActorProvider);
    }

    @Test
    void createDocumentSuggestionShouldCheckCspDuplicateWhenCspCodeGiven() {
        DocumentSuggestionModel created = new DocumentSuggestionModel(1L, UUID.randomUUID(), null, CspCode.FONC,
            CODE_CIN, Instant.now());
        when(repository.existsByCspCodeAndLabelIgnoreCase(CspCode.FONC, CODE_CIN)).thenReturn(false);
        when(repository.create(DocumentSuggestionScope.CSP, null, CspCode.FONC, CODE_CIN)).thenReturn(created);

        DocumentSuggestionModel result = service.createDocumentSuggestion(null, CspCode.FONC, CODE_CIN);

        assertEquals(created, result);
        verify(repository).existsByCspCodeAndLabelIgnoreCase(CspCode.FONC, CODE_CIN);
        verify(repository, never()).existsByEnterpriseTypeAndLabelIgnoreCase(null, CODE_CIN);
    }

    @Test
    void createDocumentSuggestionShouldThrowWhenCspLabelAlreadyExists() {
        when(repository.existsByCspCodeAndLabelIgnoreCase(CspCode.FONC, CODE_CIN)).thenReturn(true);

        assertThrows(DocumentSuggestionException.class,
            () -> service.createDocumentSuggestion(null, CspCode.FONC, CODE_CIN));
        verify(repository, never()).create(DocumentSuggestionScope.CSP, null, CspCode.FONC, CODE_CIN);
    }

    @Test
    void createDocumentSuggestionShouldRejectMutuallyExclusiveEnterpriseTypeAndCspCode() {
        assertThrows(DocumentSuggestionException.class,
            () -> service.createDocumentSuggestion(EnterpriseType.GE, CspCode.FONC, CODE_CIN));
        verify(repository, never()).existsByCspCodeAndLabelIgnoreCase(CspCode.FONC, CODE_CIN);
        verify(repository, never()).existsByEnterpriseTypeAndLabelIgnoreCase(EnterpriseType.GE, CODE_CIN);
    }

    @Test
    void getSuggestionsForCspShouldDelegateToRepository() {
        DocumentSuggestionModel suggestion = new DocumentSuggestionModel(1L, UUID.randomUUID(), null, CspCode.FONC,
            CODE_CIN, Instant.now());
        when(repository.findApplicableToCsp(CspCode.FONC)).thenReturn(List.of(suggestion));

        List<DocumentSuggestionModel> result = service.findSuggestions(EnterpriseType.PME, CspCode.FONC);

        assertEquals(1, result.size());
        assertEquals(suggestion, result.get(0));
        verify(repository, never()).findApplicableTo(EnterpriseType.PME);
    }

    @Test
    void findSuggestionsShouldFilterByEnterpriseTypeWhenNoCspCode() {
        when(repository.findApplicableTo(EnterpriseType.PME)).thenReturn(List.of());

        service.findSuggestions(EnterpriseType.PME, null);

        verify(repository).findApplicableTo(EnterpriseType.PME);
    }

    @Test
    void findSuggestionsShouldReturnAllWhenNoFilter() {
        when(repository.findAll()).thenReturn(List.of());

        service.findSuggestions(null, null);

        verify(repository).findAll();
    }
}
