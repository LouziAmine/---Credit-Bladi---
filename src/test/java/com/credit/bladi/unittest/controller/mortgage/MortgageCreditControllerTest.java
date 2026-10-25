package com.credit.bladi.unittest.controller.mortgage;

import static com.credit.bladi.unittest.mock.mortgage.MockMortgageCredit.AMOUNT;
import static com.credit.bladi.unittest.mock.mortgage.MockMortgageCredit.APPLICABLE_RATE;
import static com.credit.bladi.unittest.mock.mortgage.MockMortgageCredit.CSP_CODE;
import static com.credit.bladi.unittest.mock.mortgage.MockMortgageCredit.requestDTO;
import static com.credit.bladi.unittest.mock.mortgage.MockMortgageCredit.response;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.controller.dto.mortgage.response.MortgageCreditResponseDTO;
import com.credit.bladi.controller.mortgage.MortgageCreditController;
import com.credit.bladi.exception.BAMException;
import com.credit.bladi.service.mortgage.IMortgageCreditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class MortgageCreditControllerTest {
    @Mock
    private IMortgageCreditService simulationService;

    private MortgageCreditController controller;

    @BeforeEach
    void init() {
        controller = new MortgageCreditController(simulationService);
    }

    @Test
    void shouldReturn200WithSimulationBody() {
        when(simulationService.simulate(any())).thenReturn(response());

        ResponseEntity<MortgageCreditResponseDTO> result = controller.simulate(requestDTO());

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(CSP_CODE, result.getBody().cspCode());
        assertEquals(0, result.getBody().amount().compareTo(AMOUNT));
        assertEquals(0, result.getBody().applicableRate().compareTo(APPLICABLE_RATE));
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
