package com.credit.bladi.unittest.controller.workflow;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_ACTIVE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CLEAN;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_RISK_ANALYSIS_STEP;
import static com.credit.bladi.unittest.mock.data.MockFiles.FILE_PIECE_IDENTITE_PDF;
import static com.credit.bladi.unittest.mock.data.MockFiles.FILE_STORED_PDF;
import static com.credit.bladi.unittest.mock.data.MockFiles.MIME_APPLICATION_PDF;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_PIECE_D_IDENTITE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER1;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.controller.dto.workflow.response.DemandDocumentResponseDTO;
import com.credit.bladi.controller.workflow.DocumentController;
import com.credit.bladi.controller.workflow.mapper.UploadedFileMapper;
import com.credit.bladi.model.enums.DocumentStatus;
import com.credit.bladi.model.enums.ScanStatus;
import com.credit.bladi.model.workflow.DemandDocumentModel;
import com.credit.bladi.model.workflow.DownloadableDocument;
import com.credit.bladi.service.workflow.document.IDocumentService;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class DocumentControllerTest {
    @Mock
    private IDocumentService documentService;

    @Mock
    private IWorkflowEngineService workflowEngineService;

    private DocumentController controller;

    private static final UUID DEMAND_PUBLIC_ID = UUID.randomUUID();
    private static final UUID DOCUMENT_PUBLIC_ID = UUID.randomUUID();

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        controller = new DocumentController(documentService, workflowEngineService);
    }

    private static DemandDocumentModel document(Path storedPath) {
        return new DemandDocumentModel(5L, DOCUMENT_PUBLIC_ID, 1L, CODE_RISK_ANALYSIS_STEP,
            LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, FILE_PIECE_IDENTITE_PDF, storedPath.getFileName().toString(),
            MIME_APPLICATION_PDF, 128L, "abc123", LABEL_PIECE_D_IDENTITE, DocumentStatus.ACTIVE, ScanStatus.CLEAN, null,
            null,
            USER_MANAGER1, Instant.now());
    }

    @Test
    void shouldResolveDemandThenUploadDocument() {
        when(workflowEngineService.resolveDemandId(DEMAND_PUBLIC_ID)).thenReturn(1L);
        MockMultipartFile file = new MockMultipartFile("file", FILE_PIECE_IDENTITE_PDF, MIME_APPLICATION_PDF,
            "content".getBytes(StandardCharsets.UTF_8));
        DemandDocumentModel model = document(tempDir.resolve(FILE_STORED_PDF));
        when(documentService.upload(1L, UploadedFileMapper.toUploadedFile(file),
            LABEL_PIECE_D_IDENTITE)).thenReturn(model);

        ResponseEntity<DemandDocumentResponseDTO> response = controller.upload(DEMAND_PUBLIC_ID, file,
            LABEL_PIECE_D_IDENTITE);

        assertEquals(DOCUMENT_PUBLIC_ID, response.getBody().id());
        assertEquals(FILE_PIECE_IDENTITE_PDF, response.getBody().originalFilename());
        assertEquals(CODE_ACTIVE, response.getBody().status());
        verify(documentService).upload(1L, UploadedFileMapper.toUploadedFile(file), LABEL_PIECE_D_IDENTITE);
    }

    @Test
    void shouldListDocumentsForResolvedDemand() {
        when(workflowEngineService.resolveDemandId(DEMAND_PUBLIC_ID)).thenReturn(1L);
        DemandDocumentModel model = document(tempDir.resolve(FILE_STORED_PDF));
        when(documentService.list(1L)).thenReturn(List.of(model));

        ResponseEntity<List<DemandDocumentResponseDTO>> response = controller.list(DEMAND_PUBLIC_ID);

        assertEquals(1, response.getBody().size());
        assertEquals(CODE_CLEAN, response.getBody().get(0).scanStatus());
        verify(documentService).list(1L);
    }

    @Test
    void shouldReturnEmptyListWhenDemandHasNoDocuments() {
        when(workflowEngineService.resolveDemandId(DEMAND_PUBLIC_ID)).thenReturn(1L);
        when(documentService.list(1L)).thenReturn(List.of());

        ResponseEntity<List<DemandDocumentResponseDTO>> response = controller.list(DEMAND_PUBLIC_ID);

        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void shouldServeDecryptedDocumentContentAsAttachment() throws IOException {
        byte[] content = "%PDF-1.5\n%%EOF".getBytes(StandardCharsets.UTF_8);
        when(workflowEngineService.resolveDemandId(DEMAND_PUBLIC_ID)).thenReturn(1L);
        when(documentService.resolveDocumentId(DOCUMENT_PUBLIC_ID)).thenReturn(5L);
        DemandDocumentModel model = document(tempDir.resolve(FILE_STORED_PDF));
        when(documentService.download(1L, 5L)).thenReturn(new DownloadableDocument(model, content));

        ResponseEntity<Resource> response = controller.download(DEMAND_PUBLIC_ID, DOCUMENT_PUBLIC_ID);

        assertEquals(200, response.getStatusCode().value());
        assertArrayEquals(content, response.getBody().getContentAsByteArray());
        assertEquals(MIME_APPLICATION_PDF, response.getHeaders().getContentType().toString());
        assertTrue(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION).contains(FILE_PIECE_IDENTITE_PDF));
        verify(documentService).download(1L, 5L);
    }
}
