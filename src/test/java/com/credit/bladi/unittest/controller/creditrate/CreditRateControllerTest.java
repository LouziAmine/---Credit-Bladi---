package com.credit.bladi.unittest.controller.creditrate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.creditrate.CreditRateController;
import com.credit.bladi.controller.dto.creditrate.request.PatchCreditRateDTO;
import com.credit.bladi.controller.dto.creditrate.response.CreditRateResponseDTO;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.simulation.CreditRateModel;
import com.credit.bladi.service.creditrate.ICreditRateService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CreditRateControllerTest {

    @Mock
    private ICreditRateService creditRateService;

    private CreditRateController controller;

    @BeforeEach
    void init() {
        controller = new CreditRateController(creditRateService);
    }

    private static CreditRateModel model() {
        return CreditRateModel.getBuilder().id(1L).cspCode(CspCode.SALA).active(true).build();
    }

    @Test
    void shouldReturn200WithPageOfCreditRates() {
        CreditPage<CreditRateModel> page = new CreditPage<>();
        page.setItems(List.of(model()));
        page.setCurrentPage(1);
        page.setLastPage(1);
        page.setSize(20);
        page.setTotal(1);
        when(creditRateService.getAllParameters(1, 20)).thenReturn(page);

        ResponseEntity<CreditPage<CreditRateResponseDTO>> result = controller.getAllCreditRates(1, 20);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().getItems().size());
        assertEquals("SALA", result.getBody().getItems().get(0).getCspCode());
    }

    @Test
    void shouldReturn200WithCreditRateById() {
        when(creditRateService.getById(1L)).thenReturn(model());

        ResponseEntity<CreditRateResponseDTO> result = controller.getById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("SALA", result.getBody().getCspCode());
    }

    @Test
    void shouldReturn204AfterPatchingCreditRate() {
        PatchCreditRateDTO dto = new PatchCreditRateDTO();
        dto.setRateMin(new BigDecimal("3.50"));

        ResponseEntity<Void> result = controller.patchCreditRate(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(creditRateService).patchParameters(any(CreditRateModel.class));
    }

    @Test
    void shouldDelegateGetAllCreditRatesToService() {
        when(creditRateService.getAllParameters(anyInt(), anyInt())).thenReturn(new CreditPage<>());

        controller.getAllCreditRates(2, 10);

        verify(creditRateService).getAllParameters(eq(2), eq(10));
    }
}
