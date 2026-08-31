package com.credit.bladi.unittest.controller.workflow;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_FULFILLED;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_PENDING;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_PIECE_D_IDENTITE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.credit.bladi.controller.dto.workflow.request.CreateDocumentRequestRequestDTO;
import com.credit.bladi.controller.dto.workflow.response.DocumentRequestResponseDTO;
import com.credit.bladi.controller.workflow.DocumentRequestController;
import com.credit.bladi.model.enums.DocumentRequestStatus;
import com.credit.bladi.model.workflow.DocumentRequestModel;
import com.credit.bladi.service.workflow.document.IDocumentRequestService;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class DocumentRequestControllerTest {
    @Mock
    private IDocumentRequestService documentRequestService;

    @Mock
    private IWorkflowEngineService workflowEngineService;

    private DocumentRequestController controller;

    @BeforeEach
    void setUp() {
        controller = new DocumentRequestController(documentRequestService, workflowEngineService);
    }

    private static DocumentRequestModel request(DocumentRequestStatus status) {
        return new DocumentRequestModel(1L, UUID.randomUUID(), 1L, null, LABEL_PIECE_D_IDENTITE, status, USER_MANAGER1,
            Instant.now(), null, null, null);
    }

    @Test
    void shouldCreateRequestAndReturnIt() {
        UUID publicId = UUID.randomUUID();
        CreateDocumentRequestRequestDTO dto = new CreateDocumentRequestRequestDTO(LABEL_PIECE_D_IDENTITE);
        when(workflowEngineService.resolveDemandId(publicId)).thenReturn(1L);
        when(documentRequestService.createRequest(1L, LABEL_PIECE_D_IDENTITE))
            .thenReturn(request(DocumentRequestStatus.PENDING));

        ResponseEntity<DocumentRequestResponseDTO> response = controller.createRequest(publicId, dto);

        assertEquals(LABEL_PIECE_D_IDENTITE, response.getBody().documentType());
        assertEquals(CODE_PENDING, response.getBody().status());
    }

    @Test
    void shouldListRequestsForDemand() {
        UUID publicId = UUID.randomUUID();
        when(workflowEngineService.resolveDemandId(publicId)).thenReturn(1L);
        when(documentRequestService.listForDemand(1L)).thenReturn(List.of(request(DocumentRequestStatus.FULFILLED)));

        ResponseEntity<List<DocumentRequestResponseDTO>> response = controller.list(publicId);

        assertEquals(1, response.getBody().size());
        assertEquals(CODE_FULFILLED, response.getBody().get(0).status());
    }
}
