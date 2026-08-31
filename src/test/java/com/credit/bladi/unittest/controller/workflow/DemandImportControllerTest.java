package com.credit.bladi.unittest.controller.workflow;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_COMPLETED;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_CORP;
import static com.credit.bladi.unittest.mock.data.MockFiles.FILE_DEMANDS_XLSX;
import static com.credit.bladi.unittest.mock.data.MockFiles.MIME_APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_SP;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_MISSING_SIRET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.controller.dto.workflow.response.DemandImportReportResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandImportStartResponseDTO;
import com.credit.bladi.controller.workflow.DemandImportController;
import com.credit.bladi.controller.workflow.mapper.UploadedFileMapper;
import com.credit.bladi.exception.DemandImportException;
import com.credit.bladi.exception.DemandImportException.DemandImportExceptionType;
import com.credit.bladi.model.importdemand.DemandImportReport;
import com.credit.bladi.model.importdemand.DemandImportRowErrorModel;
import com.credit.bladi.service.importdemand.IDemandImportService;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class DemandImportControllerTest {
    @Mock
    private IDemandImportService demandImportService;

    private DemandImportController controller;

    @BeforeEach
    void setUp() {
        controller = new DemandImportController(demandImportService);
    }

    private static MockMultipartFile xlsxFile() {
        return new MockMultipartFile("file", FILE_DEMANDS_XLSX,
            MIME_APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_SP,
            "content".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void shouldStartImportAndReturnAcceptedWithJobExecutionId() {
        MockMultipartFile file = xlsxFile();
        when(demandImportService.startImport(UploadedFileMapper.toUploadedFile(file))).thenReturn(42L);

        ResponseEntity<DemandImportStartResponseDTO> response = controller.startImport(file);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(42L, response.getBody().jobExecutionId());
        verify(demandImportService).startImport(UploadedFileMapper.toUploadedFile(file));
    }

    @Test
    void shouldPropagateJobAlreadyRunningExceptionFromService() {
        MockMultipartFile file = xlsxFile();
        when(demandImportService.startImport(UploadedFileMapper.toUploadedFile(file)))
            .thenThrow(DemandImportException.of(DemandImportExceptionType.JOB_ALREADY_RUNNING));

        DemandImportException exception = assertThrows(DemandImportException.class,
            () -> controller.startImport(file));

        assertEquals(DemandImportExceptionType.JOB_ALREADY_RUNNING, exception.getType());
    }

    @Test
    void shouldReturnReportForGivenJobExecutionId() {
        DemandImportRowErrorModel rowError = new DemandImportRowErrorModel(1L, 42L, 5, COMPANY_ATLAS_CORP,
            LABEL_MISSING_SIRET, Instant.now());
        DemandImportReport report = new DemandImportReport(42L, CODE_COMPLETED, 50L, 47L, 3L,
            LocalDateTime.now().minusMinutes(5), LocalDateTime.now(), List.of(rowError));
        when(demandImportService.getReport(42L)).thenReturn(report);

        ResponseEntity<DemandImportReportResponseDTO> response = controller.getReport(42L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(42L, response.getBody().jobExecutionId());
        assertEquals(CODE_COMPLETED, response.getBody().status());
        assertEquals(50L, response.getBody().readCount());
        assertEquals(47L, response.getBody().writeCount());
        assertEquals(3L, response.getBody().skipCount());
        assertEquals(1, response.getBody().rowErrors().size());
        assertEquals(LABEL_MISSING_SIRET, response.getBody().rowErrors().get(0).errorMessage());
        verify(demandImportService).getReport(42L);
    }

    @Test
    void shouldPropagateNotFoundWhenJobExecutionDoesNotExist() {
        when(demandImportService.getReport(99L))
            .thenThrow(DemandImportException.of(DemandImportExceptionType.JOB_EXECUTION_NOT_FOUND));

        DemandImportException exception = assertThrows(DemandImportException.class,
            () -> controller.getReport(99L));

        assertEquals(DemandImportExceptionType.JOB_EXECUTION_NOT_FOUND, exception.getType());
    }
}
