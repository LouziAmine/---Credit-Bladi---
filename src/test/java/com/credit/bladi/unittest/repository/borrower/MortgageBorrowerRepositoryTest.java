package com.credit.bladi.unittest.repository.borrower;

import static com.credit.bladi.unittest.mock.data.MockDates.DATE_2026_10_11T10_00_00Z;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ANALYST1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.datasource.borrower.MortgageBorrowerDatasource;
import com.credit.bladi.datasource.borrower.entity.MortgageBorrowerEntity;
import com.credit.bladi.model.borrower.MortgageBorrowerModel;
import com.credit.bladi.model.enums.BorrowerLinkOrigin;
import com.credit.bladi.repository.DuplicateEntryException;
import com.credit.bladi.repository.borrower.MortgageBorrowerRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MortgageBorrowerRepositoryTest {
    private static final Instant NOW = Instant.parse(DATE_2026_10_11T10_00_00Z);

    @Mock
    private MortgageBorrowerDatasource datasource;

    private MortgageBorrowerRepository repository;

    @BeforeEach
    void setUp() {
        repository = new MortgageBorrowerRepository(datasource, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    private static MortgageBorrowerEntity entity(Long demandId, Long userId) {
        MortgageBorrowerEntity entity = new MortgageBorrowerEntity();
        entity.setDemandId(demandId);
        entity.setUserId(userId);
        entity.setOrigin(BorrowerLinkOrigin.EMAIL_INVITATION);
        entity.setLinkedBy(USER_ANALYST1);
        entity.setLinkedAt(NOW);
        return entity;
    }

    @Test
    void shouldRecordWhoBecameTheBorrowerAndWhen() {
        when(datasource.existsById(7L)).thenReturn(false);

        MortgageBorrowerModel linked = repository.link(7L, 9L, BorrowerLinkOrigin.EMAIL_INVITATION, USER_ANALYST1);

        assertEquals(new MortgageBorrowerModel(7L, 9L, BorrowerLinkOrigin.EMAIL_INVITATION, USER_ANALYST1, NOW),
            linked);
        verify(datasource).saveAndFlush(any(MortgageBorrowerEntity.class));
    }

    @Test
    void shouldRefuseASecondBorrowerForTheSameDossier() {
        when(datasource.existsById(7L)).thenReturn(true);

        assertThrows(DuplicateEntryException.class,
            () -> repository.link(7L, 10L, BorrowerLinkOrigin.AUTO_MATCHED_ON_CLASSIFICATION, USER_ANALYST1));
        verify(datasource, never()).saveAndFlush(any());
    }

    @Test
    void shouldListTheDossiersOfAClientMostRecentFirst() {
        when(datasource.findByUserIdOrderByLinkedAtDesc(9L)).thenReturn(List.of(entity(7L, 9L), entity(3L, 9L)));

        assertEquals(List.of(7L, 3L), repository.findDemandIdsByUserId(9L));
    }
}
