package com.credit.bladi.unittest.repository.creditrate;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_SALA;
import static com.credit.bladi.unittest.mock.mortgage.MockMortgageCredit.RATE_ID;
import static com.credit.bladi.unittest.mock.mortgage.MockMortgageCredit.creditRateEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.datasource.creditrate.CreditRateDatasource;
import com.credit.bladi.datasource.creditrate.entity.CreditRateEntity;
import com.credit.bladi.exception.CreditRateException;
import com.credit.bladi.model.creditrate.CreditRateModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.repository.creditrate.CreditRateRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class CreditRateRepositoryTest {
    @Mock
    private CreditRateDatasource datasource;

    private CreditRateRepository repository;

    @BeforeEach
    void init() {
        repository = new CreditRateRepository(datasource);
    }

    @Test
    void shouldReturnCreditRateModelWhenFound() {
        when(datasource.findActiveByCspCode(CODE_SALA)).thenReturn(Optional.of(creditRateEntity()));

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
        when(datasource.findActiveByCspCode(CODE_SALA)).thenReturn(Optional.empty());

        Optional<CreditRateModel> result = repository.findActiveByCspCode(CspCode.SALA);

        assertTrue(result.isEmpty());
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
}
