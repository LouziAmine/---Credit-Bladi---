package com.credit.bladi.unittest.repository.documentsuggestion;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.credit.bladi.datasource.documentsuggestion.DocumentSuggestionDatasource;
import com.credit.bladi.datasource.documentsuggestion.entity.DocumentSuggestionEntity;
import com.credit.bladi.datasource.documentsuggestion.mapper.DocumentSuggestionEntityMapper;
import com.credit.bladi.model.documentsuggestion.DocumentSuggestionModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.DocumentSuggestionScope;
import com.credit.bladi.repository.documentsuggestion.DocumentSuggestionRepository;
import java.time.Clock;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentSuggestionRepositoryTest {
    @Mock
    private DocumentSuggestionDatasource datasource;

    private DocumentSuggestionRepository repository;

    @BeforeEach
    void setUp() {
        repository = new DocumentSuggestionRepository(datasource, Clock.systemUTC());
    }

    @Test
    void findApplicableToCspShouldDelegateToDatasourceAndMapResults() {
        DocumentSuggestionEntity entity = DocumentSuggestionEntityMapper.mapToNewEntity(DocumentSuggestionScope.CSP,
            null,
            CspCode.FONC, CODE_CIN);
        entity.setId(1L);
        when(datasource.findApplicableToCsp(CspCode.FONC)).thenReturn(List.of(entity));

        List<DocumentSuggestionModel> result = repository.findApplicableToCsp(CspCode.FONC);

        assertEquals(1, result.size());
        assertEquals(CspCode.FONC, result.get(0).cspCode());
        assertEquals(CODE_CIN, result.get(0).label());
    }

    @Test
    void existsByCspCodeAndLabelIgnoreCaseShouldDelegateToDatasource() {
        when(datasource.existsByCspCodeAndLabelIgnoreCase(CspCode.FONC, CODE_CIN)).thenReturn(true);

        assertEquals(true, repository.existsByCspCodeAndLabelIgnoreCase(CspCode.FONC, CODE_CIN));
    }

    @Test
    void createShouldScopeToCspWhenCspCodeGiven() {
        DocumentSuggestionModel result = repository.create(DocumentSuggestionScope.CSP, null, CspCode.FONC, CODE_CIN);

        assertEquals(CspCode.FONC, result.cspCode());
    }
}
