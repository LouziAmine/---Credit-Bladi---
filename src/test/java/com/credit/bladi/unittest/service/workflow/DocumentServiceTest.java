package com.credit.bladi.unittest.service.workflow;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1500000;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_RISK_ANALYSIS;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_RISK_ANALYSIS_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.REFERENCE_DEM_ABCDEF12;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL;
import static com.credit.bladi.unittest.mock.data.MockFiles.FILE_ID_PDF;
import static com.credit.bladi.unittest.mock.data.MockFiles.FILE_NOTE_TXT;
import static com.credit.bladi.unittest.mock.data.MockFiles.FILE_OLD_PDF;
import static com.credit.bladi.unittest.mock.data.MockFiles.FILE_OLD_STORED_PDF;
import static com.credit.bladi.unittest.mock.data.MockFiles.FILE_STORED_PDF;
import static com.credit.bladi.unittest.mock.data.MockFiles.MIME_APPLICATION_PDF;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_ANALYSE_CONTRE_RISQUE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_ANALYSTE_PAE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CONTRE_ANALYSE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_PIECE_D_IDENTITE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER1;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.exception.WorkflowException;
import com.credit.bladi.exception.WorkflowException.WorkflowExceptionType;
import com.credit.bladi.infrastructure.file.TikaFileContentInspector;
import com.credit.bladi.model.audit.AuditTarget;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.DocumentStatus;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.enums.ScanStatus;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.workflow.DemandDocumentModel;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.DemandStepModel;
import com.credit.bladi.model.workflow.DownloadableDocument;
import com.credit.bladi.model.workflow.UploadedFile;
import com.credit.bladi.repository.workflow.IDemandDocumentRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import com.credit.bladi.service.workflow.document.AntivirusScanner;
import com.credit.bladi.service.workflow.document.DocumentService;
import com.credit.bladi.service.workflow.document.DocumentStorage;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import com.credit.bladi.unittest.mock.workflow.MockDemand;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {
    private static final byte[] PDF_CONTENT = "%PDF-1.5\n%%EOF".getBytes(StandardCharsets.UTF_8);

    @Mock
    private IDemandDocumentRepository repository;

    @Mock
    private IWorkflowEngineService workflowEngineService;

    @Mock
    private ICurrentActorProvider currentActorProvider;

    @Mock
    private IAuditService auditService;

    @Mock
    private AntivirusScanner antivirusScanner;

    @Mock
    private DocumentStorage storage;

    private DocumentService service(long maxFileSizeBytes, boolean antivirusEnabled, boolean withScanner) {
        return new DocumentService(repository, workflowEngineService, currentActorProvider, auditService,
            withScanner ? Optional.of(antivirusScanner) : Optional.empty(), storage, new TikaFileContentInspector(),
            List.of(MIME_APPLICATION_PDF), maxFileSizeBytes, antivirusEnabled, Clock.systemUTC(),
            new TransactionTemplate(mock(PlatformTransactionManager.class)));
    }

    private void stubUploadContext() {
        when(workflowEngineService.getDemand(1L)).thenReturn(demand());
        lenient().when(workflowEngineService.getCurrentStep(1L)).thenReturn(step());
        lenient().when(currentActorProvider.getCurrentUsername()).thenReturn(USER_MANAGER1);
    }

    private void stubSaveDocumentEcho() {
        when(repository.save(any())).thenAnswer(invocation -> {
            DemandDocumentModel toSave = invocation.getArgument(0);
            return new DemandDocumentModel(1L, UUID.randomUUID(), toSave.demandId(), toSave.stepCode(),
                toSave.stepName(), toSave.originalFilename(), toSave.storedFilename(), toSave.contentType(),
                toSave.sizeBytes(), toSave.checksumSha256(), toSave.documentType(), toSave.status(),
                toSave.scanStatus(), null, null, toSave.uploadedBy(), toSave.uploadedAt());
        });
    }

    @Test
    void shouldStoreDocumentWithSkippedScanWhenAntivirusDisabled() {
        stubUploadContext();
        stubSaveDocumentEcho();
        DocumentService service = service(1_000_000L, false, false);
        UploadedFile file = new UploadedFile(FILE_ID_PDF, PDF_CONTENT);

        DemandDocumentModel saved = service.upload(1L, file, LABEL_PIECE_D_IDENTITE);

        assertEquals(ScanStatus.SKIPPED, saved.scanStatus());
        assertEquals(DocumentStatus.ACTIVE, saved.status());
        assertEquals(MIME_APPLICATION_PDF, saved.contentType());
        verify(storage).store(eq(1L), eq(saved.storedFilename()), eq(PDF_CONTENT));
        verify(auditService).log(eq(USER_MANAGER1), eq(AuditAction.DEMAND_DOCUMENT_UPLOADED), any(AuditTarget.class),
            any());
    }

    @Test
    void shouldRejectFileTypeNotInWhitelist() {
        stubUploadContext();
        DocumentService service = service(1_000_000L, false, false);
        UploadedFile file = new UploadedFile(FILE_NOTE_TXT, "not a pdf".getBytes(StandardCharsets.UTF_8));

        WorkflowException ex = assertThrows(WorkflowException.class, () -> service.upload(1L, file, null));

        assertEquals(WorkflowExceptionType.FILE_TYPE_NOT_ALLOWED, ex.getType());
        verify(repository, never()).save(any());
    }

    @Test
    void shouldRejectFileExceedingMaxSize() {
        stubUploadContext();
        DocumentService service = service(5L, false, false);
        UploadedFile file = new UploadedFile(FILE_ID_PDF, PDF_CONTENT);

        WorkflowException ex = assertThrows(WorkflowException.class, () -> service.upload(1L, file, null));

        assertEquals(WorkflowExceptionType.FILE_TOO_LARGE, ex.getType());
    }

    @Test
    void shouldRejectAndAuditInfectedFileWithoutPersistingIt() {
        stubUploadContext();
        when(antivirusScanner.isInfected(any())).thenReturn(true);
        DocumentService service = service(1_000_000L, true, true);
        UploadedFile file = new UploadedFile(FILE_ID_PDF, PDF_CONTENT);

        WorkflowException ex = assertThrows(WorkflowException.class, () -> service.upload(1L, file, null));

        assertEquals(WorkflowExceptionType.FILE_INFECTED, ex.getType());
        verify(auditService).log(eq(USER_MANAGER1), eq(AuditAction.DEMAND_DOCUMENT_REJECTED_INFECTED),
            any(AuditTarget.class), any());
        verify(storage, never()).store(any(), any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void shouldStoreDocumentAsCleanWhenAntivirusPasses() {
        stubUploadContext();
        stubSaveDocumentEcho();
        when(antivirusScanner.isInfected(any())).thenReturn(false);
        DocumentService service = service(1_000_000L, true, true);
        UploadedFile file = new UploadedFile(FILE_ID_PDF, PDF_CONTENT);

        DemandDocumentModel saved = service.upload(1L, file, null);

        assertEquals(ScanStatus.CLEAN, saved.scanStatus());
    }

    @Test
    void shouldSupersedePreviousActiveDocumentOfSameType() {
        stubUploadContext();
        stubSaveDocumentEcho();
        DemandDocumentModel existing = new DemandDocumentModel(9L, UUID.randomUUID(), 1L, CODE_RISK_ANALYSIS_STEP,
            LABEL_CONTRE_ANALYSE, FILE_OLD_PDF, FILE_OLD_STORED_PDF, MIME_APPLICATION_PDF, 10L, "checksum",
            LABEL_PIECE_D_IDENTITE,
            DocumentStatus.ACTIVE, ScanStatus.SKIPPED, null, null, USER_MANAGER1, Instant.now());
        when(repository.findActiveByType(1L, LABEL_PIECE_D_IDENTITE)).thenReturn(Optional.of(existing));
        DocumentService service = service(1_000_000L, false, false);
        UploadedFile file = new UploadedFile(FILE_ID_PDF, PDF_CONTENT);

        DemandDocumentModel saved = service.upload(1L, file, LABEL_PIECE_D_IDENTITE);

        verify(repository, times(1)).supersede(9L, saved.id());
    }

    @Test
    void shouldBlockDownloadWhenDocumentNotClean() {
        when(workflowEngineService.getDemand(1L)).thenReturn(demand());
        DemandDocumentModel pending = new DemandDocumentModel(2L, UUID.randomUUID(), 1L, CODE_RISK_ANALYSIS_STEP,
            LABEL_CONTRE_ANALYSE, FILE_ID_PDF, FILE_STORED_PDF, MIME_APPLICATION_PDF, 10L, "checksum", null,
            DocumentStatus.ACTIVE, ScanStatus.PENDING, null, null, USER_MANAGER1, Instant.now());
        when(repository.findById(1L, 2L)).thenReturn(Optional.of(pending));
        DocumentService service = service(1_000_000L, false, false);

        WorkflowException ex = assertThrows(WorkflowException.class, () -> service.download(1L, 2L));

        assertEquals(WorkflowExceptionType.DOCUMENT_NOT_READY, ex.getType());
    }

    @Test
    void shouldReturnDownloadableDocumentWhenClean() {
        when(workflowEngineService.getDemand(1L)).thenReturn(demand());
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_MANAGER1);
        DemandDocumentModel clean = new DemandDocumentModel(2L, UUID.randomUUID(), 1L, CODE_RISK_ANALYSIS_STEP,
            LABEL_CONTRE_ANALYSE, FILE_ID_PDF, FILE_STORED_PDF, MIME_APPLICATION_PDF, 10L, "checksum", null,
            DocumentStatus.ACTIVE, ScanStatus.CLEAN, null, null, USER_MANAGER1, Instant.now());
        when(repository.findById(1L, 2L)).thenReturn(Optional.of(clean));
        DocumentService service = service(1_000_000L, false, false);

        when(storage.load(1L, FILE_STORED_PDF)).thenReturn(PDF_CONTENT);

        DownloadableDocument downloadable = service.download(1L, 2L);

        assertEquals(clean, downloadable.document());
        assertArrayEquals(PDF_CONTENT, downloadable.content());
        verify(auditService).log(eq(USER_MANAGER1), eq(AuditAction.DEMAND_DOCUMENT_DOWNLOADED), any(AuditTarget.class),
            any());
    }

    @Test
    void shouldThrowDocumentNotFoundForUnknownDocumentId() {
        when(workflowEngineService.getDemand(1L)).thenReturn(demand());
        when(repository.findById(1L, 99L)).thenReturn(Optional.empty());
        DocumentService service = service(1_000_000L, false, false);

        WorkflowException ex = assertThrows(WorkflowException.class, () -> service.download(1L, 99L));

        assertEquals(WorkflowExceptionType.DOCUMENT_NOT_FOUND, ex.getType());
    }

    @Test
    void shouldListDocumentsForDemand() {
        when(workflowEngineService.getDemand(1L)).thenReturn(demand());
        when(repository.findByDemandId(1L)).thenReturn(List.of());
        DocumentService service = service(1_000_000L, false, false);

        List<DemandDocumentModel> documents = service.list(1L);

        assertEquals(0, documents.size());
        verify(workflowEngineService).getDemand(1L);
    }

    private static DemandModel demand() {
        return MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L, COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, Money.of(AMOUNT_1500000), 84, Instant.now(), null, null, null, null,
            null, null, null, null, null, null, null, null);
    }

    private static DemandStepModel step() {
        return new DemandStepModel(CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE, CODE_RISK_ANALYSIS_STEP,
            LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE, null, null, null, null, USER_MANAGER1,
            USER_MANAGER1, Instant.now());
    }
}
