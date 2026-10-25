package com.credit.bladi.unittest.repository.bam;

import static com.credit.bladi.unittest.mock.bam.MockBamParameters.BAM_ID;
import static com.credit.bladi.unittest.mock.bam.MockBamParameters.PAGE;
import static com.credit.bladi.unittest.mock.bam.MockBamParameters.SIZE;
import static com.credit.bladi.unittest.mock.bam.MockBamParameters.bamParametersEntity;
import static com.credit.bladi.unittest.mock.bam.MockBamParameters.bamParametersModel;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_7000;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.bam.BamParametersDatasource;
import com.credit.bladi.datasource.bam.entity.BamParametersEntity;
import com.credit.bladi.exception.BamParametersException;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.repository.bam.BamParametersRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BamParametersRepositoryTest {
    @Mock
    private BamParametersDatasource datasource;

    private BamParametersRepository repository;

    @BeforeEach
    void init() {
        repository = new BamParametersRepository(datasource);
    }

    @Test
    void shouldReturnActiveParameters() {
        when(datasource.findActiveParameters()).thenReturn(Optional.of(bamParametersEntity()));

        Optional<BamParametersModel> result = repository.getActiveParameters();

        assertTrue(result.isPresent());
        assertEquals(BAM_ID, result.get().getId());
    }

    @Test
    void shouldReturnEmptyWhenNoActiveParameters() {
        when(datasource.findActiveParameters()).thenReturn(Optional.empty());

        Optional<BamParametersModel> result = repository.getActiveParameters();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldStillReturnAResultWhenMultipleActiveRowsFoundDespiteTheInvariant() {
        when(datasource.findActiveParameters()).thenReturn(Optional.of(bamParametersEntity()));
        when(datasource.countByActiveTrue()).thenReturn(2L);

        Optional<BamParametersModel> result = repository.getActiveParameters();

        assertTrue(result.isPresent());
        verify(datasource).countByActiveTrue();
    }

    @Test
    void shouldFindByIdWhenPresent() {
        when(datasource.findById(BAM_ID)).thenReturn(Optional.of(bamParametersEntity()));

        Optional<BamParametersModel> result = repository.findById(BAM_ID);

        assertTrue(result.isPresent());
        assertEquals(BAM_ID, result.get().getId());
    }

    @Test
    void shouldReturnEmptyWhenFindByIdNotFound() {
        when(datasource.findById(99L)).thenReturn(Optional.empty());

        Optional<BamParametersModel> result = repository.findById(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindAllPagedResults() {
        when(datasource.findAll(any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(bamParametersEntity())));

        CreditPage<BamParametersModel> result = repository.findAll(PAGE, SIZE);

        assertEquals(1, result.getItems().size());
        assertEquals(1, result.getTotal());
        assertEquals(PAGE, result.getCurrentPage());
        assertEquals(SIZE, result.getSize());
    }

    @Test
    void shouldThrowBamParametersExceptionWhenPatchTargetNotFound() {
        when(datasource.findById(BAM_ID)).thenReturn(Optional.empty());

        assertThrows(BamParametersException.class,
            () -> repository.patchParameters(bamParametersModel()));
    }

    @Test
    void shouldSaveAfterMergingPatchedEntity() {
        when(datasource.findById(BAM_ID)).thenReturn(Optional.of(bamParametersEntity()));
        when(datasource.save(any())).thenReturn(bamParametersEntity());

        repository.patchParameters(bamParametersModel());

        verify(datasource).save(any());
    }

    @Test
    void shouldNotDeactivateOthersWhenPatchedEntityIsNotActive() {
        BamParametersEntity inactive = bamParametersEntity();
        inactive.setActive(false);
        when(datasource.findById(BAM_ID)).thenReturn(Optional.of(inactive));
        BamParametersModel patchWithoutActiveField = BamParametersModel.getBuilder().id(BAM_ID).build();

        repository.patchParameters(patchWithoutActiveField);

        verify(datasource, never()).deactivateAllExcept(anyLong());
    }

    @Test
    void shouldRejectPatchLeavingMinProcessingFeesAboveMax() {
        when(datasource.findById(BAM_ID)).thenReturn(Optional.of(bamParametersEntity()));
        BamParametersModel patch = BamParametersModel.getBuilder()
            .id(BAM_ID)
            .minProcessingFees(Money.of(AMOUNT_7000))
            .build();

        BamParametersException exception = assertThrows(BamParametersException.class,
            () -> repository.patchParameters(patch));

        assertEquals(BamParametersException.BamParametersExceptionType.INVALID_PROCESSING_FEES_RANGE,
            exception.getType());
    }
}
