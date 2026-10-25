package com.credit.bladi.unittest.controller.client;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_INITIATION_STEP;
import static com.credit.bladi.unittest.mock.data.MockFiles.FILE_ID_PDF;
import static com.credit.bladi.unittest.mock.data.MockFiles.FILE_UUID_PDF;
import static com.credit.bladi.unittest.mock.data.MockFiles.MIME_APPLICATION_PDF;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_INITIATION;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_PIECE_D_IDENTITE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDUPONT;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.controller.client.ClientDocumentRequestController;
import com.credit.bladi.controller.dto.workflow.response.DemandDocumentResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DocumentRequestResponseDTO;
import com.credit.bladi.controller.workflow.mapper.UploadedFileMapper;
import com.credit.bladi.model.enums.DocumentRequestStatus;
import com.credit.bladi.model.enums.DocumentStatus;
import com.credit.bladi.model.enums.ScanStatus;
import com.credit.bladi.model.workflow.DemandDocumentModel;
import com.credit.bladi.model.workflow.DocumentRequestModel;
import com.credit.bladi.service.security.ClientDemandAccessGuard;
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
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class ClientDocumentRequestControllerTest {
    @Mock
    private IDocumentRequestService documentRequestService;

    @Mock
    private IWorkflowEngineService workflowEngineService;

    @Mock
    private ClientDemandAccessGuard accessGuard;

    private ClientDocumentRequestController controller;

    @BeforeEach
    void setUp() {
        controller = new ClientDocumentRequestController(documentRequestService, workflowEngineService, accessGuard);
    }

    @Test
    void shouldAssertApprovedThenListRequests() {
        UUID publicId = UUID.randomUUID();
        when(workflowEngineService.resolveDemandId(publicId)).thenReturn(1L);
        when(documentRequestService.listForDemand(1L)).thenReturn(List.of(
            new DocumentRequestModel(1L, UUID.randomUUID(), 1L, null, LABEL_PIECE_D_IDENTITE,
            DocumentRequestStatus.PENDING,
                USER_MANAGER1, Instant.now(), null, null, null)));

        ResponseEntity<List<DocumentRequestResponseDTO>> response = controller.list(publicId);

        verify(accessGuard).assertApprovedForDemand(1L);
        assertEquals(1, response.getBody().size());
    }

    @Test
    void shouldAssertApprovedThenUploadForRequest() {
        UUID publicId = UUID.randomUUID();
        UUID requestPublicId = UUID.randomUUID();
        when(workflowEngineService.resolveDemandId(publicId)).thenReturn(1L);
        when(documentRequestService.resolveRequestId(requestPublicId)).thenReturn(3L);
        MockMultipartFile file = new MockMultipartFile("file", FILE_ID_PDF, MIME_APPLICATION_PDF, new byte[]{1});
        DemandDocumentModel uploaded = new DemandDocumentModel(7L, UUID.randomUUID(), 1L, CODE_INITIATION_STEP,
            LABEL_INITIATION, FILE_ID_PDF, FILE_UUID_PDF, MIME_APPLICATION_PDF, 1L, "checksum", LABEL_PIECE_D_IDENTITE,
            DocumentStatus.ACTIVE, ScanStatus.CLEAN, null, null, USER_JDUPONT, Instant.now());
        when(documentRequestService.uploadForRequest(1L, 3L,
            UploadedFileMapper.toUploadedFile(file))).thenReturn(uploaded);

        ResponseEntity<DemandDocumentResponseDTO> response = controller.uploadForRequest(publicId, requestPublicId,
            file);

        verify(accessGuard).assertApprovedForDemand(1L);
        assertEquals(FILE_ID_PDF, response.getBody().originalFilename());
    }
}
