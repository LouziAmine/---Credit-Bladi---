package com.credit.bladi.unittest.controller.bam;

import static com.credit.bladi.unittest.mock.bam.MockBamParameters.BAM_ID;
import static com.credit.bladi.unittest.mock.bam.MockBamParameters.BAM_PUBLIC_ID;
import static com.credit.bladi.unittest.mock.bam.MockBamParameters.SIZE;
import static com.credit.bladi.unittest.mock.bam.MockBamParameters.TOTAL;
import static com.credit.bladi.unittest.mock.bam.MockBamParameters.bamParametersModel;
import static com.credit.bladi.unittest.mock.bam.MockBamParameters.bamParametersPage;
import static com.credit.bladi.unittest.mock.bam.MockBamParameters.patchRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.bam.BamParametersController;
import com.credit.bladi.controller.dto.bam.response.BamParametersResponseDTO;
import com.credit.bladi.service.bamparameters.IBamParametersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class BamParametersControllerTest {
    @Mock
    private IBamParametersService bamParametersService;

    private BamParametersController controller;

    @BeforeEach
    void init() {
        controller = new BamParametersController(bamParametersService);
    }

    @Test
    void shouldReturn200WithPagedParameters() {
        when(bamParametersService.getAllParameters(1, SIZE)).thenReturn(bamParametersPage());

        ResponseEntity<CreditPage<BamParametersResponseDTO>> result = controller.getAllParameters(1, SIZE);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().getItems().size());
        assertEquals(TOTAL, result.getBody().getTotal());
    }

    @Test
    void shouldDelegateToServiceWithGivenPageAndSize() {
        when(bamParametersService.getAllParameters(2, 10)).thenReturn(bamParametersPage());

        controller.getAllParameters(2, 10);

        verify(bamParametersService).getAllParameters(2, 10);
    }

    @Test
    void shouldReturn200WithParametersById() {
        when(bamParametersService.resolveBamParametersId(BAM_PUBLIC_ID)).thenReturn(BAM_ID);
        when(bamParametersService.getById(BAM_ID)).thenReturn(bamParametersModel());

        ResponseEntity<BamParametersResponseDTO> result = controller.getById(BAM_PUBLIC_ID);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(BAM_PUBLIC_ID, result.getBody().id());
    }

    @Test
    void shouldReturn204OnPatchParameters() {
        when(bamParametersService.resolveBamParametersId(BAM_PUBLIC_ID)).thenReturn(BAM_ID);
        when(bamParametersService.patchParameters(any())).thenReturn(bamParametersModel());

        ResponseEntity<Void> result = controller.patchParameters(BAM_PUBLIC_ID, patchRequest());

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(bamParametersService).patchParameters(any());
    }
}
