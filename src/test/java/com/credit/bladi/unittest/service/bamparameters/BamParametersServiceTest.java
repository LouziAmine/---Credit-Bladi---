package com.credit.bladi.unittest.service.bamparameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.exception.BamParametersException;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.bam.BamParametersPatchResult;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Ratio;
import com.credit.bladi.repository.bam.IBamParametersRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.bamparameters.BamParametersService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BamParametersServiceTest {

    @Mock
    private IBamParametersRepository repository;

    @Mock
    private IAuditService auditService;

    @Mock
    private ICurrentActorProvider currentActorProvider;

    private BamParametersService service;

    @BeforeEach
    void setUp() {
        service = new BamParametersService(repository, auditService, currentActorProvider);
    }

    @Test
    void shouldDelegateToRepositoryAndReturnPagedResult() {
        BamParametersModel model = BamParametersModel.getBuilder()
            .id(1L)
            .processingFeesRatio(Ratio.of(new BigDecimal("0.01")))
            .minProcessingFees(Money.of(new BigDecimal("1000")))
            .maxProcessingFees(Money.of(new BigDecimal("10000")))
            .registrationFeeRatio(Ratio.of(new BigDecimal("0.01")))
            .landConservationRatio(Ratio.of(new BigDecimal("0.015")))
            .notaryFeeRatio(Ratio.of(new BigDecimal("0.01")))
            .active(true)
            .build();
        CreditPage<BamParametersModel> page = new CreditPage<>();
        page.setItems(List.of(model));
        page.setTotal(1);
        page.setCurrentPage(1);
        page.setSize(10);
        when(repository.findAll(1, 10)).thenReturn(page);

        CreditPage<BamParametersModel> result = service.getAllParameters(1, 10);

        assertEquals(1, result.getItems().size());
        assertEquals(1, result.getTotal());
        verify(repository).findAll(1, 10);
    }

    @Test
    void shouldReturnParametersByIdWhenFound() {
        BamParametersModel model = BamParametersModel.getBuilder().id(1L).active(true).build();
        when(repository.findById(1L)).thenReturn(Optional.of(model));

        BamParametersModel result = service.getById(1L);

        assertEquals(model, result);
    }

    @Test
    void shouldThrowWhenParametersByIdNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BamParametersException.class, () -> service.getById(99L));
    }

    @Test
    void shouldDelegatePatchParametersToRepository() {
        BamParametersModel patch = BamParametersModel.getBuilder()
            .id(1L)
            .processingFeesRatio(Ratio.of(new BigDecimal("0.005")))
            .build();
        BamParametersModel before = BamParametersModel.getBuilder()
            .id(1L)
            .processingFeesRatio(Ratio.of(new BigDecimal("0.003")))
            .build();
        BamParametersModel after = BamParametersModel.getBuilder()
            .id(1L)
            .processingFeesRatio(Ratio.of(new BigDecimal("0.005")))
            .build();
        when(repository.patchParameters(patch)).thenReturn(new BamParametersPatchResult(before, after));

        BamParametersModel result = service.patchParameters(patch);

        assertEquals(after, result);
        verify(repository).patchParameters(patch);
    }
}
