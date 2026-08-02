package com.credit.bladi.unittest.controller.simulation;

import static com.credit.bladi.unittest.mock.simulation.MockSimulation.AMOUNT;
import static com.credit.bladi.unittest.mock.simulation.MockSimulation.APPLICABLE_RATE;
import static com.credit.bladi.unittest.mock.simulation.MockSimulation.CSP_CODE;
import static com.credit.bladi.unittest.mock.simulation.MockSimulation.requestDTO;
import static com.credit.bladi.unittest.mock.simulation.MockSimulation.response;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.controller.dto.simulation.response.SimulationResponseDTO;
import com.credit.bladi.controller.simulation.SimulationController;
import com.credit.bladi.exception.BAMException;
import com.credit.bladi.service.simulation.ISimulationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class SimulationControllerTest {

    @Mock
    private ISimulationService simulationService;

    private SimulationController controller;

    @BeforeEach
    void init() {
        controller = new SimulationController(simulationService);
    }

    @Test
    void shouldReturn200WithSimulationBody() {
        when(simulationService.simulate(any())).thenReturn(response());

        ResponseEntity<SimulationResponseDTO> result = controller.simulate(requestDTO());

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(CSP_CODE, result.getBody().getCspCode());
        assertEquals(0, result.getBody().getAmount().compareTo(AMOUNT));
        assertEquals(0, result.getBody().getApplicableRate().compareTo(APPLICABLE_RATE));
    }

    @Test
    void shouldDelegateToSimulationService() {
        when(simulationService.simulate(any())).thenReturn(response());

        controller.simulate(requestDTO());

        verify(simulationService).simulate(any());
    }

    @Test
    void shouldPropagateBAMExceptionFromService() {
        when(simulationService.simulate(any()))
            .thenThrow(BAMException.of(BAMException.BAMExceptionType.DURATION_LIMIT_EXCEEDED));

        assertThrows(BAMException.class, () -> controller.simulate(requestDTO()));
    }
}