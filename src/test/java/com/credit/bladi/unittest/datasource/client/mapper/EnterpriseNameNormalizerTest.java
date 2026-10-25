package com.credit.bladi.unittest.datasource.client.mapper;

import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SA;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARLAU;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL_AU;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_SA_ATLAS_TEXTILE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_AFD_TECHNOLOGY;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_ATLAS_TEXTILE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.credit.bladi.datasource.client.mapper.EnterpriseNameNormalizer;
import org.junit.jupiter.api.Test;

class EnterpriseNameNormalizerTest {
    @Test
    void shouldUppercaseAndTrim() {
        assertEquals(LABEL_AFD_TECHNOLOGY, EnterpriseNameNormalizer.normalize("  afd technology  "));
    }

    @Test
    void shouldTreatDashesAndPunctuationAsWhitespace() {
        assertEquals(LABEL_AFD_TECHNOLOGY, EnterpriseNameNormalizer.normalize("afd-technology"));
        assertEquals(LABEL_AFD_TECHNOLOGY, EnterpriseNameNormalizer.normalize("afd: technology"));
        assertEquals(LABEL_AFD_TECHNOLOGY, EnterpriseNameNormalizer.normalize("afd, technology."));
    }

    @Test
    void shouldStripAccents() {
        assertEquals(LABEL_ATLAS_TEXTILE, EnterpriseNameNormalizer.normalize("Atlàs Téxtîle"));
    }

    @Test
    void shouldStripTrailingLegalFormSuffix() {
        assertEquals(LABEL_ATLAS_TEXTILE, EnterpriseNameNormalizer.normalize(COMPANY_ATLAS_TEXTILE_SARL));
        assertEquals(LABEL_ATLAS_TEXTILE, EnterpriseNameNormalizer.normalize(COMPANY_ATLAS_TEXTILE_SA));
        assertEquals(LABEL_ATLAS_TEXTILE, EnterpriseNameNormalizer.normalize(COMPANY_ATLAS_TEXTILE_SARLAU));
        assertEquals(LABEL_ATLAS_TEXTILE, EnterpriseNameNormalizer.normalize(COMPANY_ATLAS_TEXTILE_SARL_AU));
    }

    @Test
    void shouldNotStripLegalFormTokenWhenNotTrailing() {
        assertEquals("SA ATLAS TEXTILE", EnterpriseNameNormalizer.normalize(COMPANY_SA_ATLAS_TEXTILE));
    }

    @Test
    void shouldCollapseMultipleSeparatorsIntoSingleSpace() {
        assertEquals(LABEL_AFD_TECHNOLOGY, EnterpriseNameNormalizer.normalize("afd   --   technology"));
    }

    @Test
    void shouldReturnEmptyStringForNullOrBlank() {
        assertEquals("", EnterpriseNameNormalizer.normalize(null));
        assertEquals("", EnterpriseNameNormalizer.normalize("   "));
    }
}
