package com.credit.bladi.unittest.controller.mourabaha;

import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_10;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_25;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_50;
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
class MourabahaParametersControllerTest {
    @Mock
    private IMourabahaParametersService mourabahaParametersService;

    private static final UUID PUBLIC_ID = UUID.randomUUID();

    private MourabahaParametersController controller;

    @BeforeEach
    void init() {
        controller = new MourabahaParametersController(mourabahaParametersService);
    }

    private static MourabahaParametersModel model() {
        return MourabahaParametersModel.getBuilder()
            .id(1L)
            .publicId(PUBLIC_ID)
            .profitRate(Rate.of(DECIMAL_5_25))
            .vatRate(Ratio.of(DECIMAL_0_10))
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
        assertEquals(PUBLIC_ID, result.getBody().getItems().get(0).id());
    }

    @Test
    void shouldReturn200WithParametersById() {
        when(mourabahaParametersService.resolveMourabahaParametersId(PUBLIC_ID)).thenReturn(1L);
        when(mourabahaParametersService.getById(1L)).thenReturn(model());

        ResponseEntity<MourabahaParametersResponseDTO> result = controller.getById(PUBLIC_ID);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(PUBLIC_ID, result.getBody().id());
    }

    @Test
    void shouldReturn204AfterPatchingParameters() {
        PatchMourabahaParametersDTO dto = new PatchMourabahaParametersDTO(DECIMAL_5_50, null, null, null);
        when(mourabahaParametersService.resolveMourabahaParametersId(PUBLIC_ID)).thenReturn(1L);

        ResponseEntity<Void> result = controller.patchParameters(PUBLIC_ID, dto);

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
