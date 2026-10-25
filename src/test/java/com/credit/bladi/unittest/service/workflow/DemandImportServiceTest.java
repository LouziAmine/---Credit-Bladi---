package com.credit.bladi.unittest.service.workflow;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_COMPLETED;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_CORP;
import static com.credit.bladi.unittest.mock.data.MockFiles.FILE_BIG_XLSX;
import static com.credit.bladi.unittest.mock.data.MockFiles.FILE_DEMANDS_XLSX;
import static com.credit.bladi.unittest.mock.data.MockFiles.FILE_EMPTY_XLSX;
import static com.credit.bladi.unittest.mock.data.MockFiles.FILE_NOTES_TXT;
import static com.credit.bladi.unittest.mock.data.MockFiles.MIME_APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_SP;
import static com.credit.bladi.unittest.mock.data.MockFiles.MIME_TEXT_PLAIN;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_MISSING_SIRET;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.controller.workflow.mapper.UploadedFileMapper;
import com.credit.bladi.exception.DemandImportException;
import com.credit.bladi.exception.DemandImportException.DemandImportExceptionType;
import com.credit.bladi.infrastructure.file.TikaFileContentInspector;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.importdemand.DemandImportProgress;
import com.credit.bladi.model.importdemand.DemandImportReport;
import com.credit.bladi.model.importdemand.DemandImportRowErrorModel;
import com.credit.bladi.repository.importdemand.IDemandImportRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.importdemand.DemandImportService;
import com.credit.bladi.service.importdemand.IDemandImportJobLauncher;
import com.credit.bladi.service.security.ICurrentActorProvider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class DemandImportServiceTest {
    @Mock
    private IDemandImportJobLauncher jobLauncher;

    @Mock
    private IDemandImportRepository importRepository;

    @Mock
    private IAuditService auditService;

    @Mock
    private ICurrentActorProvider currentActorProvider;

    @TempDir
    Path storageDir;

    private DemandImportService service;

    @BeforeEach
    void setUp() {
        service = new DemandImportService(jobLauncher, importRepository,
            auditService, currentActorProvider, storageDir.toString(), 10_000_000L, new TikaFileContentInspector(),
            Clock.systemUTC());
    }

    private static byte[] validXlsxBytes() throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.createSheet("Demands");
            workbook.write(out);
            return out.toByteArray();
        }
    }

    @Test
    void shouldRejectEmptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", FILE_EMPTY_XLSX,
            MIME_APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_SP, new byte[0]);

        DemandImportException exception = assertThrows(DemandImportException.class,
            () -> service.startImport(UploadedFileMapper.toUploadedFile(file)));

        assertEquals(DemandImportExceptionType.FILE_EMPTY, exception.getType());
    }

    @Test
    void shouldRejectFileExceedingMaxSize() {
        service = new DemandImportService(jobLauncher, importRepository,
            auditService, currentActorProvider, storageDir.toString(), 5L, new TikaFileContentInspector(),
            Clock.systemUTC());
        MockMultipartFile file = new MockMultipartFile("file", FILE_BIG_XLSX,
            MIME_APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_SP,
            "way more than five bytes".getBytes(StandardCharsets.UTF_8));

        DemandImportException exception = assertThrows(DemandImportException.class,
            () -> service.startImport(UploadedFileMapper.toUploadedFile(file)));

        assertEquals(DemandImportExceptionType.FILE_TOO_LARGE, exception.getType());
    }

    @Test
    void shouldRejectFileThatIsNotAReadableSpreadsheet() {
        MockMultipartFile file = new MockMultipartFile("file", FILE_NOTES_TXT, MIME_TEXT_PLAIN,
            "just some plain text content, definitely not a workbook".getBytes(StandardCharsets.UTF_8));

        DemandImportException exception = assertThrows(DemandImportException.class,
            () -> service.startImport(UploadedFileMapper.toUploadedFile(file)));

        assertEquals(DemandImportExceptionType.FILE_TYPE_NOT_ALLOWED, exception.getType());
    }

    @Test
    void shouldRejectWhenAnImportIsAlreadyRunning() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", FILE_DEMANDS_XLSX,
            MIME_APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_SP, validXlsxBytes());
        when(jobLauncher.isRunning()).thenReturn(true);

        DemandImportException exception = assertThrows(DemandImportException.class,
            () -> service.startImport(UploadedFileMapper.toUploadedFile(file)));

        assertEquals(DemandImportExceptionType.JOB_ALREADY_RUNNING, exception.getType());
    }

    @Test
    void shouldStartImportPersistFileAndAuditLogOnSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", FILE_DEMANDS_XLSX,
            MIME_APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_SP, validXlsxBytes());
        when(jobLauncher.isRunning()).thenReturn(false);
        when(jobLauncher.launch(any(Path.class), any(Instant.class))).thenReturn(100L);
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_MANAGER1);

        Long jobExecutionId = service.startImport(UploadedFileMapper.toUploadedFile(file));

        assertEquals(100L, jobExecutionId);
        verify(auditService).log(eq(USER_MANAGER1), eq(AuditAction.DEMAND_IMPORT_STARTED),
            contains("jobExecutionId=100"));

        ArgumentCaptor<Path> fileCaptor = ArgumentCaptor.forClass(Path.class);
        verify(jobLauncher).launch(fileCaptor.capture(), any(Instant.class));
        String filePath = fileCaptor.getValue().toString();
        assertTrue(filePath.startsWith(storageDir.toString()));
        assertTrue(filePath.endsWith(".xlsx"));
        assertTrue(Files.exists(Path.of(filePath)));
    }

    @Test
    void shouldNotAuditWhenTheJobCannotStart() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", FILE_DEMANDS_XLSX,
            MIME_APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_SP, validXlsxBytes());
        when(jobLauncher.isRunning()).thenReturn(false);
        when(jobLauncher.launch(any(Path.class), any(Instant.class))).thenThrow(new IllegalStateException("boom"));

        assertThrows(IllegalStateException.class, () -> service.startImport(UploadedFileMapper.toUploadedFile(file)));
        verify(auditService, never()).log(any(), any(), any());
    }

    @Test
    void shouldReturnReportCombiningJobProgressAndRowErrors() {
        LocalDateTime start = LocalDateTime.now().minusMinutes(5);
        LocalDateTime end = LocalDateTime.now();
        when(jobLauncher.findProgress(42L))
            .thenReturn(Optional.of(new DemandImportProgress(CODE_COMPLETED, 50L, 47L, 3L, start, end)));
        DemandImportRowErrorModel rowError = new DemandImportRowErrorModel(1L, 42L, 5, COMPANY_ATLAS_CORP,
            LABEL_MISSING_SIRET, Instant.now());
        when(importRepository.findRowErrors(42L)).thenReturn(List.of(rowError));

        DemandImportReport report = service.getReport(42L);

        assertEquals(42L, report.jobExecutionId());
        assertEquals(CODE_COMPLETED, report.status());
        assertEquals(50L, report.readCount());
        assertEquals(47L, report.writeCount());
        assertEquals(3L, report.skipCount());
        assertEquals(start, report.startTime());
        assertEquals(end, report.endTime());
        assertEquals(1, report.rowErrors().size());
    }

    @Test
    void shouldThrowWhenJobExecutionNotFound() {
        when(jobLauncher.findProgress(99L)).thenReturn(Optional.empty());

        DemandImportException exception = assertThrows(DemandImportException.class,
            () -> service.getReport(99L));

        assertEquals(DemandImportExceptionType.JOB_EXECUTION_NOT_FOUND, exception.getType());
    }
}
