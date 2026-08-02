package com.credit.bladi.unittest.controller.mourabaha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.mourabaha.request.PatchMourabahaParametersDTO;
import com.credit.bladi.controller.dto.mourabaha.response.MourabahaParametersResponseDTO;
import com.credit.bladi.controller.mourabaha.MourabahaParametersController;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import com.credit.bladi.service.mourabahaparameters.IMourabahaParametersService;
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
class MourabahaParametersControllerTest {

    @Mock
    private IMourabahaParametersService mourabahaParametersService;

    private MourabahaParametersController controller;

    @BeforeEach
    void init() {
        controller = new MourabahaParametersController(mourabahaParametersService);
    }

    private static MourabahaParametersModel model() {
        return MourabahaParametersModel.getBuilder()
            .id(1L)
            .profitRate(Rate.of(new BigDecimal("5.25")))
            .vatRate(Ratio.of(new BigDecimal("0.10")))
            .active(true)
            .build();
    }

    @Test
    void shouldReturn200WithPageOfParameters() {
        CreditPage<MourabahaParametersModel> page = new CreditPage<>();
        page.setItems(List.of(model()));
        page.setCurrentPage(1);
        page.setLastPage(1);
        page.setSize(20);
        page.setTotal(1);
        when(mourabahaParametersService.getAllParameters(1, 20)).thenReturn(page);

        ResponseEntity<CreditPage<MourabahaParametersResponseDTO>> result = controller.getAllParameters(1, 20);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().getItems().size());
        assertEquals(1L, result.getBody().getItems().get(0).getId());
    }

    @Test
    void shouldReturn200WithParametersById() {
        when(mourabahaParametersService.getById(1L)).thenReturn(model());

        ResponseEntity<MourabahaParametersResponseDTO> result = controller.getById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1L, result.getBody().getId());
    }

    @Test
    void shouldReturn204AfterPatchingParameters() {
        PatchMourabahaParametersDTO dto = new PatchMourabahaParametersDTO();
        dto.setProfitRate(new BigDecimal("5.50"));

        ResponseEntity<Void> result = controller.patchParameters(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(mourabahaParametersService).patchParameters(any(MourabahaParametersModel.class));
    }

    @Test
    void shouldDelegateGetAllParametersToService() {
        when(mourabahaParametersService.getAllParameters(anyInt(), anyInt())).thenReturn(new CreditPage<>());

        controller.getAllParameters(2, 10);

        verify(mourabahaParametersService).getAllParameters(eq(2), eq(10));
    }
}