package com.credit.bladi.unittest.service.sector;

import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_TEXTILE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.exception.ActivitySectorException;
import com.credit.bladi.exception.ActivitySectorException.ActivitySectorExceptionType;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.sector.ActivitySectorModel;
import com.credit.bladi.repository.sector.IActivitySectorRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.sector.ActivitySectorService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActivitySectorServiceTest {
    @Mock
    private IActivitySectorRepository repository;

    @Mock
    private IAuditService auditService;

    @Mock
    private ICurrentActorProvider currentActorProvider;

    private ActivitySectorService activitySectorService;

    @BeforeEach
    void setUp() {
        activitySectorService = new ActivitySectorService(repository, auditService, currentActorProvider);
    }

    @Test
    void shouldCreateActivitySectorAndAuditLogIt() {
        ActivitySectorModel created = new ActivitySectorModel(1L, UUID.randomUUID(), LABEL_TEXTILE, Instant.now());
        when(repository.existsByNameIgnoreCase(LABEL_TEXTILE)).thenReturn(false);
        when(repository.create(LABEL_TEXTILE)).thenReturn(created);
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        ActivitySectorModel result = activitySectorService.createActivitySector(LABEL_TEXTILE);

        assertEquals(created, result);
        verify(auditService).log(eq(USER_ADMIN), eq(AuditAction.ACTIVITY_SECTOR_CREATE), any());
    }

    @Test
    void shouldRejectDuplicateNameCaseInsensitively() {
        when(repository.existsByNameIgnoreCase("textile")).thenReturn(true);

        ActivitySectorException ex = assertThrows(ActivitySectorException.class,
            () -> activitySectorService.createActivitySector("textile"));

        assertEquals(ActivitySectorExceptionType.DUPLICATE_NAME, ex.getType());
        verify(repository, never()).create(any());
    }

    @Test
    void shouldReturnAllActivitySectors() {
        List<ActivitySectorModel> sectors =
            List.of(new ActivitySectorModel(1L, UUID.randomUUID(), LABEL_TEXTILE, Instant.now()));
        when(repository.findAll()).thenReturn(sectors);

        List<ActivitySectorModel> result = activitySectorService.getAllActivitySectors();

        assertEquals(sectors, result);
    }
}
