package com.credit.bladi.unittest.repository.mourabaha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.mourabaha.MourabahaParametersDatasource;
import com.credit.bladi.datasource.mourabaha.entity.MourabahaParametersEntity;
import com.credit.bladi.exception.MourabahaParametersException;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import com.credit.bladi.model.mourabaha.MourabahaParametersPatchResult;
import com.credit.bladi.repository.mourabaha.MourabahaParametersRepository;
import java.math.BigDecimal;
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
class MourabahaParametersRepositoryTest {

    @Mock
    private MourabahaParametersDatasource datasource;

    private MourabahaParametersRepository repository;

    @BeforeEach
    void init() {
        repository = new MourabahaParametersRepository(datasource);
    }

    private static MourabahaParametersEntity entity() {
        MourabahaParametersEntity entity = new MourabahaParametersEntity();
        entity.setId(1L);
        entity.setProfitRate(new BigDecimal("5.25"));
        entity.setVatRate(new BigDecimal("0.10"));
        entity.setActive(true);
        return entity;
    }

    @Test
    void shouldReturnPageOfParameters() {
        Page<MourabahaParametersEntity> page = new PageImpl<>(List.of(entity()));
        when(datasource.findAll(any(PageRequest.class))).thenReturn(page);

        CreditPage<MourabahaParametersModel> result = repository.findAll(1, 20);

        assertEquals(1, result.getItems().size());
        assertEquals(1L, result.getItems().get(0).getId());
    }

    @Test
    void shouldReturnModelWhenFoundById() {
        when(datasource.findById(1L)).thenReturn(Optional.of(entity()));

        Optional<MourabahaParametersModel> result = repository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void shouldReturnEmptyWhenNotFoundById() {
        when(datasource.findById(1L)).thenReturn(Optional.empty());

        Optional<MourabahaParametersModel> result = repository.findById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnActiveParametersWhenFound() {
        when(datasource.countByActiveTrue()).thenReturn(1L);
        when(datasource.findActiveParameters()).thenReturn(Optional.of(entity()));

        Optional<MourabahaParametersModel> result = repository.getActiveParameters();

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void shouldReturnEmptyActiveParametersWhenNoneFound() {
        when(datasource.countByActiveTrue()).thenReturn(0L);
        when(datasource.findActiveParameters()).thenReturn(Optional.empty());

        Optional<MourabahaParametersModel> result = repository.getActiveParameters();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldWarnWhenMultipleActiveParametersFound() {
        when(datasource.countByActiveTrue()).thenReturn(2L);
        when(datasource.findActiveParameters()).thenReturn(Optional.of(entity()));

        Optional<MourabahaParametersModel> result = repository.getActiveParameters();

        assertTrue(result.isPresent());
    }

    @Test
    void shouldPatchParametersAndDeactivateOthersWhenActive() {
        MourabahaParametersEntity existing = entity();
        when(datasource.findById(1L)).thenReturn(Optional.of(existing));

        MourabahaParametersModel patch = MourabahaParametersModel.getBuilder()
            .id(1L)
            .profitRate(com.credit.bladi.model.vo.Rate.of(new BigDecimal("5.50")))
            .active(true)
            .build();

        MourabahaParametersPatchResult result = repository.patchParameters(patch);

        assertEquals(0, new BigDecimal("5.25").compareTo(result.before().getProfitRate().value()));
        assertEquals(0, new BigDecimal("5.50").compareTo(result.after().getProfitRate().value()));
        verify(datasource).deactivateAllExcept(1L);
        verify(datasource).save(existing);
    }

    @Test
    void shouldPatchParametersWithoutDeactivatingWhenNotActive() {
        MourabahaParametersEntity existing = new MourabahaParametersEntity();
        existing.setId(1L);
        existing.setActive(false);
        when(datasource.findById(1L)).thenReturn(Optional.of(existing));

        MourabahaParametersModel patch = MourabahaParametersModel.getBuilder().id(1L).build();

        repository.patchParameters(patch);

        verify(datasource, never()).deactivateAllExcept(anyLong());
        verify(datasource).save(existing);
    }

    @Test
    void shouldThrowNotFoundWhenPatchingUnknownId() {
        when(datasource.findById(1L)).thenReturn(Optional.empty());

        MourabahaParametersModel patch = MourabahaParametersModel.getBuilder().id(1L).build();

        assertThrows(MourabahaParametersException.class, () -> repository.patchParameters(patch));
        verify(datasource, never()).save(any());
    }
}
