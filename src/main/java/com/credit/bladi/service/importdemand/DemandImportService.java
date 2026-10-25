package com.credit.bladi.service.importdemand;

import static com.credit.bladi.exception.DemandImportException.DemandImportExceptionType.FILE_EMPTY;
import static com.credit.bladi.exception.DemandImportException.DemandImportExceptionType.FILE_TOO_LARGE;
import static com.credit.bladi.exception.DemandImportException.DemandImportExceptionType.FILE_TYPE_NOT_ALLOWED;
import static com.credit.bladi.exception.DemandImportException.DemandImportExceptionType.JOB_ALREADY_RUNNING;
import static com.credit.bladi.exception.DemandImportException.DemandImportExceptionType.JOB_EXECUTION_NOT_FOUND;

import com.credit.bladi.exception.DemandImportException;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.importdemand.DemandImportProgress;
import com.credit.bladi.model.importdemand.DemandImportReport;
import com.credit.bladi.model.importdemand.DemandImportRowErrorModel;
import com.credit.bladi.model.workflow.UploadedFile;
import com.credit.bladi.repository.importdemand.IDemandImportRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.file.FileContentInspector;
import com.credit.bladi.service.security.ICurrentActorProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DemandImportService implements IDemandImportService {
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/x-tika-ooxml");

    private final IDemandImportJobLauncher jobLauncher;

    private final IDemandImportRepository importRepository;

    private final IAuditService auditService;

    private final ICurrentActorProvider currentActorProvider;

    private final String storagePath;

    private final long maxFileSizeBytes;

    private final FileContentInspector fileInspector;

    private final Clock clock;

    public DemandImportService(IDemandImportJobLauncher jobLauncher,
                               IDemandImportRepository importRepository,
                               IAuditService auditService,
                               ICurrentActorProvider currentActorProvider,
                               @Value("${app.demand-import.storage-path}") String storagePath,
                               @Value("${app.demand-import.max-file-size-bytes}") long maxFileSizeBytes,
                               FileContentInspector fileInspector, Clock clock) {
        this.jobLauncher = jobLauncher;
        this.importRepository = importRepository;
        this.auditService = auditService;
        this.currentActorProvider = currentActorProvider;
        this.storagePath = storagePath;
        this.maxFileSizeBytes = maxFileSizeBytes;
        this.fileInspector = fileInspector;
        this.clock = clock;
    }

    @Override
    public Long startImport(UploadedFile file) {
        byte[] content = file.content();
        if (content.length == 0) {
            throw DemandImportException.of(FILE_EMPTY);
        }
        if (content.length > maxFileSizeBytes) {
            throw DemandImportException.of(FILE_TOO_LARGE);
        }
        if (!ALLOWED_CONTENT_TYPES.contains(fileInspector.detectContentType(content))
                || !fileInspector.isReadableWorkbook(content)) {
            throw DemandImportException.of(FILE_TYPE_NOT_ALLOWED);
        }
        if (jobLauncher.isRunning()) {
            throw DemandImportException.of(JOB_ALREADY_RUNNING);
        }

        Long jobExecutionId = jobLauncher.launch(writeToDisk(content), clock.instant());
        String username = currentActorProvider.getCurrentUsername();
        auditService.log(username, AuditAction.DEMAND_IMPORT_STARTED,
            "jobExecutionId=" + jobExecutionId + ", filename=" + file.originalFilename());
        return jobExecutionId;
    }

    @Override
    public void recordRowError(Long jobExecutionId, int rowNumber, String companyName, String errorMessage) {
        importRepository.recordRowError(jobExecutionId, rowNumber, companyName, errorMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public DemandImportReport getReport(Long jobExecutionId) {
        DemandImportProgress progress = jobLauncher.findProgress(jobExecutionId)
            .orElseThrow(() -> DemandImportException.of(JOB_EXECUTION_NOT_FOUND));
        List<DemandImportRowErrorModel> rowErrors = importRepository.findRowErrors(jobExecutionId);
        return new DemandImportReport(jobExecutionId, progress.status(), progress.readCount(),
            progress.writeCount(), progress.skipCount(), progress.startTime(), progress.endTime(), rowErrors);
    }

    private Path writeToDisk(byte[] content) {
        try {
            Path dir = Path.of(storagePath);
            Files.createDirectories(dir);
            Path target = dir.resolve(UUID.randomUUID() + ".xlsx");
            Files.write(target, content);
            return target;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to persist uploaded demand import file", e);
        }
    }
}
