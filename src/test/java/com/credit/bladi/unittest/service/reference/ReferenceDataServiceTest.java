package com.credit.bladi.unittest.service.reference;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_ANALYSTE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_MANAGER;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_PAE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_ANALYSTE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_PAE_POLE_ANALYSE_ET_ENGAGEMENTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.model.reference.Grade;
import com.credit.bladi.model.reference.Team;
import com.credit.bladi.repository.reference.IReferenceDataRepository;
import com.credit.bladi.service.reference.ReferenceDataService;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReferenceDataServiceTest {
    @Mock
    private IReferenceDataRepository repository;

    private ReferenceDataService referenceDataService;

    @BeforeEach
    void setUp() {
        referenceDataService = new ReferenceDataService(repository);
    }

    @Test
    void shouldReturnAllGradesFromRepository() {
        List<Grade> grades = List.of(
            new Grade(1L, UUID.randomUUID(), CODE_ANALYSTE, LABEL_ANALYSTE),
            new Grade(2L, UUID.randomUUID(), CODE_MANAGER, "Manager"));
        when(repository.findAllGrades()).thenReturn(grades);

        List<Grade> result = referenceDataService.getGrades();

        assertEquals(grades, result);
        verify(repository).findAllGrades();
    }

    @Test
    void shouldReturnEmptyListWhenNoGradesExist() {
        when(repository.findAllGrades()).thenReturn(Collections.emptyList());

        List<Grade> result = referenceDataService.getGrades();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnAllTeamsFromRepository() {
        List<Team> teams = List.of(new Team(1L, UUID.randomUUID(), CODE_PAE, LABEL_PAE_POLE_ANALYSE_ET_ENGAGEMENTS));
        when(repository.findAllTeams()).thenReturn(teams);

        List<Team> result = referenceDataService.getTeams();

        assertEquals(teams, result);
        verify(repository).findAllTeams();
    }

    @Test
    void shouldReturnEmptyListWhenNoTeamsExist() {
        when(repository.findAllTeams()).thenReturn(Collections.emptyList());

        List<Team> result = referenceDataService.getTeams();

        assertTrue(result.isEmpty());
    }
}
