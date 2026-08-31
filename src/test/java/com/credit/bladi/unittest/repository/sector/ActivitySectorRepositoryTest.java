package com.credit.bladi.unittest.repository.sector;

import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_TEXTILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.credit.bladi.datasource.sector.ActivitySectorDatasource;
import com.credit.bladi.datasource.sector.entity.ActivitySectorEntity;
import com.credit.bladi.model.sector.ActivitySectorModel;
import com.credit.bladi.repository.sector.ActivitySectorRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class ActivitySectorRepositoryTest {
    @Mock
    private ActivitySectorDatasource datasource;

    private ActivitySectorRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ActivitySectorRepository(datasource, Clock.systemUTC());
    }

    @Test
    void shouldCreateAndMapEntity() {
        when(datasource.save(ArgumentMatchers.any())).thenAnswer(invocation -> {
            ActivitySectorEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        ActivitySectorModel result = repository.create(LABEL_TEXTILE);

        assertEquals(LABEL_TEXTILE, result.name());
        assertEquals(1L, result.id());
    }

    @Test
    void shouldReturnAllSectorsSortedByName() {
        ActivitySectorEntity entity = new ActivitySectorEntity();
        entity.setId(1L);
        entity.setName(LABEL_TEXTILE);
        entity.setCreatedAt(Instant.now());
        when(datasource.findAll(Sort.by("name"))).thenReturn(List.of(entity));

        List<ActivitySectorModel> result = repository.findAll();

        assertEquals(1, result.size());
        assertEquals(LABEL_TEXTILE, result.get(0).name());
    }

    @Test
    void shouldDelegateExistsByNameIgnoreCase() {
        when(datasource.existsByNameIgnoreCase("textile")).thenReturn(true);

        assertTrue(repository.existsByNameIgnoreCase("textile"));
    }
}
