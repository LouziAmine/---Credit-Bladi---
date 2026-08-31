package com.credit.bladi.unittest.controller.reference;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_ANALYSTE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_PAE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_ANALYSTE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_PAE_POLE_ANALYSE_ET_ENGAGEMENTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.controller.dto.reference.response.GradeResponseDTO;
import com.credit.bladi.controller.dto.reference.response.TeamResponseDTO;
import com.credit.bladi.controller.reference.ReferenceDataController;
import com.credit.bladi.model.reference.Grade;
import com.credit.bladi.model.reference.Team;
import com.credit.bladi.service.reference.IReferenceDataService;
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
class ReferenceDataControllerTest {
    @Mock
    private IReferenceDataService referenceDataService;

    private ReferenceDataController controller;

    @BeforeEach
    void setUp() {
        controller = new ReferenceDataController(referenceDataService);
    }

    @Test
    void shouldReturn200WithMappedGrades() {
        UUID publicId = UUID.randomUUID();
        when(referenceDataService.getGrades())
            .thenReturn(List.of(new Grade(1L, publicId, CODE_ANALYSTE, LABEL_ANALYSTE)));

        ResponseEntity<List<GradeResponseDTO>> result = controller.getGrades();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals(publicId, result.getBody().get(0).id());
        assertEquals(CODE_ANALYSTE, result.getBody().get(0).code());
        assertEquals(LABEL_ANALYSTE, result.getBody().get(0).name());
    }

    @Test
    void shouldReturn200WithEmptyGradesWhenNoneExist() {
        when(referenceDataService.getGrades()).thenReturn(Collections.emptyList());

        ResponseEntity<List<GradeResponseDTO>> result = controller.getGrades();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().isEmpty());
        verify(referenceDataService).getGrades();
    }

    @Test
    void shouldReturn200WithMappedTeams() {
        UUID publicId = UUID.randomUUID();
        when(referenceDataService.getTeams())
            .thenReturn(List.of(new Team(1L, publicId, CODE_PAE, LABEL_PAE_POLE_ANALYSE_ET_ENGAGEMENTS)));

        ResponseEntity<List<TeamResponseDTO>> result = controller.getTeams();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals(publicId, result.getBody().get(0).id());
        assertEquals(CODE_PAE, result.getBody().get(0).code());
        assertEquals(LABEL_PAE_POLE_ANALYSE_ET_ENGAGEMENTS, result.getBody().get(0).name());
    }

    @Test
    void shouldReturn200WithEmptyTeamsWhenNoneExist() {
        when(referenceDataService.getTeams()).thenReturn(Collections.emptyList());

        ResponseEntity<List<TeamResponseDTO>> result = controller.getTeams();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().isEmpty());
        verify(referenceDataService).getTeams();
    }
}
