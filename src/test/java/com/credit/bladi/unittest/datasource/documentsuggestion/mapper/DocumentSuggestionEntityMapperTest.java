package com.credit.bladi.unittest.datasource.documentsuggestion.mapper;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CIN;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_BULLETIN_DE_PAIE_3_DERNIERS_MOIS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.credit.bladi.datasource.documentsuggestion.entity.DocumentSuggestionEntity;
import com.credit.bladi.datasource.documentsuggestion.mapper.DocumentSuggestionEntityMapper;
import com.credit.bladi.model.documentsuggestion.DocumentSuggestionModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.DocumentSuggestionScope;
import com.credit.bladi.model.enums.EnterpriseType;
import org.junit.jupiter.api.Test;

class DocumentSuggestionEntityMapperTest {
    @Test
    void mapToNewEntityShouldCopyCspFields() {
        DocumentSuggestionEntity entity = DocumentSuggestionEntityMapper.mapToNewEntity(DocumentSuggestionScope.CSP,
            null, CspCode.FONC, CODE_CIN);

        assertEquals(CspCode.FONC, entity.getCspCode());
        assertNull(entity.getEnterpriseType());
        assertEquals(CODE_CIN, entity.getLabel());
    }

    @Test
    void mapToNewEntityShouldCopyEnterpriseFields() {
        DocumentSuggestionEntity entity = DocumentSuggestionEntityMapper.mapToNewEntity(
            DocumentSuggestionScope.ENTERPRISE, EnterpriseType.PME, null, "Extrait du Registre de Commerce");

        assertEquals(EnterpriseType.PME, entity.getEnterpriseType());
        assertNull(entity.getCspCode());
    }

    @Test
    void mapFromEntityShouldRoundTripCspCode() {
        DocumentSuggestionEntity entity = DocumentSuggestionEntityMapper.mapToNewEntity(DocumentSuggestionScope.CSP,
            null, CspCode.FONC, LABEL_BULLETIN_DE_PAIE_3_DERNIERS_MOIS);
        entity.setId(1L);

        DocumentSuggestionModel model = DocumentSuggestionEntityMapper.mapFromEntity(entity);

        assertEquals(CspCode.FONC, model.cspCode());
        assertNull(model.enterpriseType());
        assertEquals(LABEL_BULLETIN_DE_PAIE_3_DERNIERS_MOIS, model.label());
    }
}
