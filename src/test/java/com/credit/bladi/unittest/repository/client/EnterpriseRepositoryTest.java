package com.credit.bladi.unittest.repository.client;

import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL_2;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_UNKNOWN_CO;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_ATLAS_TEXTILE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_TEXTILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.datasource.client.EnterpriseDatasource;
import com.credit.bladi.datasource.client.entity.EnterpriseEntity;
import com.credit.bladi.model.client.EnterpriseModel;
import com.credit.bladi.repository.client.EnterpriseRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EnterpriseRepositoryTest {
    @Mock
    private EnterpriseDatasource datasource;

    private EnterpriseRepository repository;

    @BeforeEach
    void setUp() {
        repository = new EnterpriseRepository(datasource, Clock.systemUTC());
    }

    private static EnterpriseEntity entity(long id, String name) {
        EnterpriseEntity entity = new EnterpriseEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setSector(LABEL_TEXTILE);
        entity.setCreatedAt(Instant.now());
        return entity;
    }

    @Test
    void shouldFindMatchByNormalizedName() {
        EnterpriseEntity existing = entity(1L, COMPANY_ATLAS_TEXTILE_SARL);
        when(datasource.findByNormalizedNameOrderByCreatedAtAsc(LABEL_ATLAS_TEXTILE))
            .thenReturn(List.of(existing));

        Optional<EnterpriseModel> result = repository.findMatchByName(COMPANY_ATLAS_TEXTILE_SARL_2);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().id());
    }

    @Test
    void shouldReturnOldestWhenMultipleRowsShareNormalizedName() {
        EnterpriseEntity older = entity(1L, COMPANY_ATLAS_TEXTILE_SARL);
        EnterpriseEntity newer = entity(2L, "ATLAS-TEXTILE");
        when(datasource.findByNormalizedNameOrderByCreatedAtAsc(LABEL_ATLAS_TEXTILE))
            .thenReturn(List.of(older, newer));

        Optional<EnterpriseModel> result = repository.findMatchByName(COMPANY_ATLAS_TEXTILE_SARL);

        assertEquals(1L, result.get().id());
    }

    @Test
    void shouldReturnEmptyWithoutQueryingDatasourceWhenNameNormalizesToBlank() {
        Optional<EnterpriseModel> result = repository.findMatchByName("   ---   ");

        assertTrue(result.isEmpty());
        verify(datasource, never()).findByNormalizedNameOrderByCreatedAtAsc(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldReturnEmptyWhenNoRowMatches() {
        when(datasource.findByNormalizedNameOrderByCreatedAtAsc("UNKNOWN CO")).thenReturn(List.of());

        Optional<EnterpriseModel> result = repository.findMatchByName(COMPANY_UNKNOWN_CO);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindAllByIds() {
        EnterpriseEntity first = entity(1L, COMPANY_ATLAS_TEXTILE_SARL);
        EnterpriseEntity second = entity(2L, "AFD Technology");
        when(datasource.findAllById(List.of(1L, 2L))).thenReturn(List.of(first, second));

        List<EnterpriseModel> result = repository.findAllByIds(List.of(1L, 2L));

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(2L, result.get(1).id());
    }
}
