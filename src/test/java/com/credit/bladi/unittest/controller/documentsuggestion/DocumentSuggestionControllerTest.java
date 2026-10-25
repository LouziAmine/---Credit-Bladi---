package com.credit.bladi.unittest.controller.documentsuggestion;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.controller.documentsuggestion.DocumentSuggestionController;
import com.credit.bladi.controller.dto.documentsuggestion.request.CreateDocumentSuggestionRequestDTO;
import com.credit.bladi.controller.dto.documentsuggestion.response.DocumentSuggestionResponseDTO;
import com.credit.bladi.exception.ValidationException;
import com.credit.bladi.model.documentsuggestion.DocumentSuggestionModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.service.documentsuggestion.IDocumentSuggestionService;
import java.time.Instant;
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
class DocumentSuggestionControllerTest {
    @Mock
    private IDocumentSuggestionService documentSuggestionService;

    private DocumentSuggestionController controller;

    @BeforeEach
    void init() {
        controller = new DocumentSuggestionController(documentSuggestionService);
    }

    @Test
    void createShouldPropagateValidationExceptionFromService() {
        CreateDocumentSuggestionRequestDTO requestDTO =
            new CreateDocumentSuggestionRequestDTO(EnterpriseType.PME, CspCode.FONC, CODE_CIN);
        when(documentSuggestionService.createDocumentSuggestion(EnterpriseType.PME, CspCode.FONC, CODE_CIN))
            .thenThrow(ValidationException.of(ValidationException.ValidationExceptionType.INVALID_VALUE,
                "enterpriseType/cspCode: mutually exclusive"));

        assertThrows(ValidationException.class, () -> controller.createDocumentSuggestion(requestDTO));
    }

    @Test
    void createShouldDelegateCspCodeToService() {
        CreateDocumentSuggestionRequestDTO requestDTO =
            new CreateDocumentSuggestionRequestDTO(null, CspCode.FONC, CODE_CIN);
        DocumentSuggestionModel created = new DocumentSuggestionModel(1L, UUID.randomUUID(), null, CspCode.FONC,
            CODE_CIN, Instant.now());
        when(documentSuggestionService.createDocumentSuggestion(null, CspCode.FONC, CODE_CIN)).thenReturn(created);

        ResponseEntity<DocumentSuggestionResponseDTO> response = controller.createDocumentSuggestion(requestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(CspCode.FONC, response.getBody().cspCode());
    }

    @Test
    void getDocumentSuggestionsShouldPreferCspCodeOverEnterpriseTypeWhenBothGiven() {
        DocumentSuggestionModel suggestion = new DocumentSuggestionModel(1L, UUID.randomUUID(), null, CspCode.FONC,
            CODE_CIN, Instant.now());
        when(documentSuggestionService.findSuggestions(EnterpriseType.PME, CspCode.FONC))
            .thenReturn(List.of(suggestion));

        ResponseEntity<List<DocumentSuggestionResponseDTO>> response =
            controller.getDocumentSuggestions(EnterpriseType.PME, CspCode.FONC);

        assertEquals(1, response.getBody().size());
        assertEquals(CspCode.FONC, response.getBody().get(0).cspCode());
    }

    @Test
    void getDocumentSuggestionsShouldReturnAllWhenNeitherGiven() {
        when(documentSuggestionService.findSuggestions(null, null)).thenReturn(List.of());

        controller.getDocumentSuggestions(null, null);

        verify(documentSuggestionService).findSuggestions(null, null);
    }
}
