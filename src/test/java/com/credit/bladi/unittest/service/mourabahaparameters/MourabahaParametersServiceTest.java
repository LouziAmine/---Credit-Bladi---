package com.credit.bladi.unittest.service.mourabahaparameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.exception.MourabahaParametersException;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import com.credit.bladi.model.mourabaha.MourabahaParametersPatchResult;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.repository.mourabaha.IMourabahaParametersRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.mourabahaparameters.MourabahaParametersService;
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
class MourabahaParametersServiceTest {

    @Mock
    private IMourabahaParametersRepository repository;

    @Mock
    private IAuditService auditService;

    @Mock
    private ICurrentActorProvider currentActorProvider;

    private MourabahaParametersService service;

    @BeforeEach
    void setUp() {
        service = new MourabahaParametersService(repository, auditService, currentActorProvider);
    }

    @Test
    void shouldDelegateToRepositoryAndReturnPagedResult() {
        MourabahaParametersModel model = MourabahaParametersModel.getBuilder().id(1L).active(true).build();
        CreditPage<MourabahaParametersModel> page = new CreditPage<>();
        page.setItems(List.of(model));
        page.setTotal(1);
        page.setCurrentPage(1);
        page.setSize(10);
        when(repository.findAll(1, 10)).thenReturn(page);

        CreditPage<MourabahaParametersModel> result = service.getAllParameters(1, 10);

        assertEquals(1, result.getItems().size());
        assertEquals(1, result.getTotal());
        verify(repository).findAll(1, 10);
    }

    @Test
    void shouldReturnParametersByIdWhenFound() {
        MourabahaParametersModel model = MourabahaParametersModel.getBuilder().id(1L).active(true).build();
        when(repository.findById(1L)).thenReturn(Optional.of(model));

        MourabahaParametersModel result = service.getById(1L);

        assertEquals(model, result);
    }

    @Test
    void shouldThrowWhenParametersByIdNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(MourabahaParametersException.class, () -> service.getById(99L));
    }

    @Test
    void shouldDelegatePatchParametersToRepositoryAndAuditTheChange() {
        MourabahaParametersModel patch = MourabahaParametersModel.getBuilder()
            .id(1L)
            .profitRate(Rate.of(new BigDecimal("5.50")))
            .build();
        MourabahaParametersModel before = MourabahaParametersModel.getBuilder()
            .id(1L)
            .profitRate(Rate.of(new BigDecimal("5.00")))
            .build();
        MourabahaParametersModel after = MourabahaParametersModel.getBuilder()
            .id(1L)
            .profitRate(Rate.of(new BigDecimal("5.50")))
            .build();
        when(repository.patchParameters(patch)).thenReturn(new MourabahaParametersPatchResult(before, after));
        when(currentActorProvider.getCurrentUsername()).thenReturn("jdoe");

        MourabahaParametersModel result = service.patchParameters(patch);

        assertEquals(after, result);
        verify(repository).patchParameters(patch);
        verify(auditService).log(
            eq("jdoe"), eq(AuditAction.MOURABAHA_PARAMETERS_UPDATE), contains("profitRate"));
    }
}