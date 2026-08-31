package com.credit.bladi.unittest.service.creditrate;

import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_14_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_6_00;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDOE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.exception.CreditRateException;
import com.credit.bladi.model.creditrate.CreditRateModel;
import com.credit.bladi.model.creditrate.CreditRatePatchResult;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.parameters.RegulatoryLimits;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.repository.creditrate.ICreditRateRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.creditrate.CreditRateService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreditRateServiceTest {
    @Mock
    private ICreditRateRepository repository;

    @Mock
    private IAuditService auditService;

    @Mock
    private ICurrentActorProvider currentActorProvider;

    private CreditRateService service;

    @BeforeEach
    void setUp() {
        service = new CreditRateService(repository, auditService, currentActorProvider,
            new RegulatoryLimits(Rate.of(DECIMAL_14_00)));
    }

    @Test
    void shouldDelegateToRepositoryAndReturnPagedResult() {
        CreditRateModel model = CreditRateModel.getBuilder().id(1L).cspCode(CspCode.SALA).active(true).build();
        CreditPage<CreditRateModel> page = new CreditPage<>();
        page.setItems(List.of(model));
        page.setTotal(1);
        page.setCurrentPage(1);
        page.setSize(10);
        when(repository.findAll(1, 10)).thenReturn(page);

        CreditPage<CreditRateModel> result = service.getAllParameters(1, 10);

        assertEquals(1, result.getItems().size());
        assertEquals(1, result.getTotal());
        verify(repository).findAll(1, 10);
    }

    @Test
    void shouldReturnParametersByIdWhenFound() {
        CreditRateModel model = CreditRateModel.getBuilder().id(1L).cspCode(CspCode.SALA).active(true).build();
        when(repository.findById(1L)).thenReturn(Optional.of(model));

        CreditRateModel result = service.getById(1L);

        assertEquals(model, result);
    }

    @Test
    void shouldThrowWhenParametersByIdNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CreditRateException.class, () -> service.getById(99L));
    }

    @Test
    void shouldDelegatePatchParametersToRepositoryAndAuditTheChange() {
        CreditRateModel patch = CreditRateModel.getBuilder()
            .id(1L)
            .rateMax(Rate.of(DECIMAL_6_00))
            .build();
        CreditRateModel before = CreditRateModel.getBuilder()
            .id(1L)
            .rateMax(Rate.of(DECIMAL_5_00))
            .build();
        CreditRateModel after = CreditRateModel.getBuilder()
            .id(1L)
            .rateMax(Rate.of(DECIMAL_6_00))
            .build();
        when(repository.patchParameters(patch)).thenReturn(new CreditRatePatchResult(before, after));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_JDOE);

        CreditRateModel result = service.patchParameters(patch);

        assertEquals(after, result);
        verify(repository).patchParameters(patch);
    }
}
