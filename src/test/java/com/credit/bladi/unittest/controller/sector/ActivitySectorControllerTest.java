package com.credit.bladi.unittest.controller.sector;

import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_TEXTILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.controller.dto.sector.request.CreateActivitySectorRequestDTO;
import com.credit.bladi.controller.dto.sector.response.ActivitySectorResponseDTO;
import com.credit.bladi.controller.sector.ActivitySectorController;
import com.credit.bladi.model.sector.ActivitySectorModel;
import com.credit.bladi.service.sector.IActivitySectorService;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ActivitySectorControllerTest {
    @Mock
    private IActivitySectorService activitySectorService;

    private ActivitySectorController controller;

    @BeforeEach
    void setUp() {
        controller = new ActivitySectorController(activitySectorService);
    }

    @Test
    void shouldReturn201WithCreatedActivitySectorAndLocationHeader() {
        UUID publicId = UUID.randomUUID();
        Instant createdAt = Instant.now();
        ActivitySectorModel created = new ActivitySectorModel(1L, publicId, LABEL_TEXTILE, createdAt);
        when(activitySectorService.createActivitySector(LABEL_TEXTILE)).thenReturn(created);

        ResponseEntity<ActivitySectorResponseDTO> result =
            controller.createActivitySector(new CreateActivitySectorRequestDTO(LABEL_TEXTILE));

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(publicId, result.getBody().id());
        assertEquals(LABEL_TEXTILE, result.getBody().name());
        assertEquals(createdAt, result.getBody().createdAt());
        assertNotNull(result.getHeaders().getLocation());
        assertTrue(result.getHeaders().getLocation().toString().endsWith("/api/v1/activity-sectors/" + publicId));
    }

    @Test
    void shouldDelegateCreationToService() {
        when(activitySectorService.createActivitySector(LABEL_TEXTILE))
            .thenReturn(new ActivitySectorModel(1L, UUID.randomUUID(), LABEL_TEXTILE, Instant.now()));

        controller.createActivitySector(new CreateActivitySectorRequestDTO(LABEL_TEXTILE));

        verify(activitySectorService).createActivitySector(LABEL_TEXTILE);
    }

    @Test
    void shouldReturn200WithMappedActivitySectors() {
        UUID publicId = UUID.randomUUID();
        Instant createdAt = Instant.now();
        when(activitySectorService.getAllActivitySectors())
            .thenReturn(List.of(new ActivitySectorModel(1L, publicId, LABEL_TEXTILE, createdAt)));

        ResponseEntity<List<ActivitySectorResponseDTO>> result = controller.getAllActivitySectors();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals(publicId, result.getBody().get(0).id());
        assertEquals(LABEL_TEXTILE, result.getBody().get(0).name());
        assertEquals(createdAt, result.getBody().get(0).createdAt());
    }

    @Test
    void shouldReturn200WithEmptyListWhenNoneExist() {
        when(activitySectorService.getAllActivitySectors()).thenReturn(Collections.emptyList());

        ResponseEntity<List<ActivitySectorResponseDTO>> result = controller.getAllActivitySectors();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().isEmpty());
        verify(activitySectorService).getAllActivitySectors();
    }
}
