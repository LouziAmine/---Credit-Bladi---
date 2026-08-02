package com.credit.bladi.unittest.repository.creditrate;

import static com.credit.bladi.unittest.mock.simulation.MockSimulation.RATE_ID;
import static com.credit.bladi.unittest.mock.simulation.MockSimulation.creditRateEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.datasource.creditrate.CreditRateDatasource;
import com.credit.bladi.datasource.creditrate.entity.CreditRateEntity;
import com.credit.bladi.exception.CreditRateException;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.simulation.CreditRateModel;
import com.credit.bladi.model.simulation.CreditRatePatchResult;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.repository.creditrate.CreditRateRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class CreditRateRepositoryTest {

    @Mock
    private CreditRateDatasource datasource;

    private ConcurrentMapCacheManager cacheManager;

    private CreditRateRepository repository;

    @BeforeEach
    void init() {
        cacheManager = new ConcurrentMapCacheManager("creditRates");
        repository = new CreditRateRepository(datasource, cacheManager);
    }

    @Test
    void shouldReturnCreditRateModelWhenFound() {
        when(datasource.findActiveByCspCode("SALA")).thenReturn(Optional.of(creditRateEntity()));

        Optional<CreditRateModel> result = repository.findActiveByCspCode(CspCode.SALA);

        assertTrue(result.isPresent());
        assertEquals(RATE_ID, result.get().getId());
        assertEquals(CspCode.SALA, result.get().getCspCode());
    }

    @Test
    void shouldReturnPageOfCreditRates() {
        Page<CreditRateEntity> page = new PageImpl<>(List.of(creditRateEntity()));
        when(datasource.findAll(any(PageRequest.class))).thenReturn(page);

        var result = repository.findAll(1, 20);

        assertEquals(1, result.getItems().size());
    }

    @Test
    void shouldReturnModelWhenFoundById() {
        when(datasource.findById(RATE_ID)).thenReturn(Optional.of(creditRateEntity()));

        Optional<CreditRateModel> result = repository.findById(RATE_ID);

        assertTrue(result.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenNotFoundById() {
        when(datasource.findById(RATE_ID)).thenReturn(Optional.empty());

        Optional<CreditRateModel> result = repository.findById(RATE_ID);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowNotFoundWhenPatchingUnknownId() {
        when(datasource.findById(RATE_ID)).thenReturn(Optional.empty());

        CreditRateModel patch = CreditRateModel.getBuilder().id(RATE_ID).build();

        assertThrows(CreditRateException.class, () -> repository.patchParameters(patch));
    }

    @Test
    void shouldReturnEmptyWhenCspCodeNotFound() {
        when(datasource.findActiveByCspCode("SALA")).thenReturn(Optional.empty());

        Optional<CreditRateModel> result = repository.findActiveByCspCode(CspCode.SALA);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldEvictOnlyThePatchedCspCodeFromCache() {
        Cache cache = cacheManager.getCache("creditRates");
        cache.put("SALA", Optional.of(creditRateEntity()));
        cache.put("FONC", "unrelated-untouched-entry");

        CreditRateEntity existing = creditRateEntity();
        when(datasource.findById(RATE_ID)).thenReturn(Optional.of(existing));

        CreditRateModel patch = CreditRateModel.getBuilder()
            .id(RATE_ID)
            .rateMax(Rate.of(new BigDecimal("6.00")))
            .build();

        CreditRatePatchResult result = repository.patchParameters(patch);

        assertNull(cache.get("SALA"));
        assertEquals("unrelated-untouched-entry", cache.get("FONC").get());
        assertEquals(0, new BigDecimal("6.00").compareTo(result.after().getRateMax().value()));
    }

    @Test
    void shouldNotDeactivateOthersWhenPatchedEntityIsNotActive() {
        CreditRateEntity existing = creditRateEntity();
        existing.setActive(false);
        when(datasource.findById(RATE_ID)).thenReturn(Optional.of(existing));

        CreditRateModel patch = CreditRateModel.getBuilder().id(RATE_ID).build();

        repository.patchParameters(patch);

        verify(datasource, never()).deactivateOthersForCspCode(anyString(), anyLong());
    }

    @Test
    void shouldSkipCacheEvictionWhenCacheNotConfigured() {
        CacheManager noOpCacheManager = mock(CacheManager.class);
        when(noOpCacheManager.getCache("creditRates")).thenReturn(null);
        CreditRateRepository repositoryWithoutCache = new CreditRateRepository(datasource, noOpCacheManager);
        when(datasource.findById(RATE_ID)).thenReturn(Optional.of(creditRateEntity()));

        CreditRateModel patch = CreditRateModel.getBuilder().id(RATE_ID).build();

        CreditRatePatchResult result = repositoryWithoutCache.patchParameters(patch);

        assertEquals(RATE_ID, result.after().getId());
    }
}
