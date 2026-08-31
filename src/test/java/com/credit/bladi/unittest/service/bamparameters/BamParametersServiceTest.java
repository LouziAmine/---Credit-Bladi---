package com.credit.bladi.unittest.service.bamparameters;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_10000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_003;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_005;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_01;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_015;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_14_00;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.exception.BamParametersException;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.bam.BamParametersPatchResult;
import com.credit.bladi.model.parameters.RegulatoryLimits;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import com.credit.bladi.repository.bam.IBamParametersRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.bamparameters.BamParametersService;
import com.credit.bladi.service.security.ICurrentActorProvider;
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
        service = new BamParametersService(repository, auditService, currentActorProvider,
            new RegulatoryLimits(Rate.of(DECIMAL_14_00)));
    }

    @Test
    void shouldDelegateToRepositoryAndReturnPagedResult() {
        BamParametersModel model = BamParametersModel.getBuilder()
            .id(1L)
            .processingFeesRatio(Ratio.of(DECIMAL_0_01))
            .minProcessingFees(Money.of(AMOUNT_1000))
            .maxProcessingFees(Money.of(AMOUNT_10000))
            .registrationFeeRatio(Ratio.of(DECIMAL_0_01))
            .landConservationRatio(Ratio.of(DECIMAL_0_015))
            .notaryFeeRatio(Ratio.of(DECIMAL_0_01))
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
            .processingFeesRatio(Ratio.of(DECIMAL_0_005))
            .build();
        BamParametersModel before = BamParametersModel.getBuilder()
            .id(1L)
            .processingFeesRatio(Ratio.of(DECIMAL_0_003))
            .build();
        BamParametersModel after = BamParametersModel.getBuilder()
            .id(1L)
            .processingFeesRatio(Ratio.of(DECIMAL_0_005))
            .build();
        when(repository.patchParameters(patch)).thenReturn(new BamParametersPatchResult(before, after));

        BamParametersModel result = service.patchParameters(patch);

        assertEquals(after, result);
        verify(repository).patchParameters(patch);
    }
}
