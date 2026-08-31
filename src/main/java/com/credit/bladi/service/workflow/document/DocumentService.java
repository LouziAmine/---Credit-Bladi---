package com.credit.bladi.service.workflow.document;

import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.DOCUMENT_NOT_FOUND;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.DOCUMENT_NOT_READY;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.FILE_INFECTED;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.FILE_TOO_LARGE;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.FILE_TYPE_NOT_ALLOWED;

import com.credit.bladi.exception.WorkflowException;
import com.credit.bladi.model.audit.AuditTarget;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.DocumentStatus;
import com.credit.bladi.model.enums.ScanStatus;
import com.credit.bladi.model.workflow.DemandDocumentModel;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.DemandStepModel;
import com.credit.bladi.model.workflow.DownloadableDocument;
import com.credit.bladi.model.workflow.InspectedFile;
import com.credit.bladi.model.workflow.UploadedFile;
import com.credit.bladi.repository.workflow.IDemandDocumentRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.file.FileContentInspector;
import com.credit.bladi.service.security.ICurrentActorProvider;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class DocumentService implements IDocumentService {
    private static final Logger LOGGER = LogManager.getLogger(DocumentService.class);

    private final IDemandDocumentRepository repository;

    private final IWorkflowEngineService workflowEngineService;

    private final ICurrentActorProvider currentActorProvider;

    private final IAuditService auditService;

    private final Optional<AntivirusScanner> antivirusScanner;

    private final DocumentStorage storage;

    private final FileContentInspector fileInspector;

    private final Set<String> allowedContentTypes;

    private final long maxFileSizeBytes;

    private final boolean antivirusEnabled;

    private final Clock clock;

    private final TransactionTemplate transactionTemplate;

    public DocumentService(IDemandDocumentRepository repository,
                           IWorkflowEngineService workflowEngineService,
                           ICurrentActorProvider currentActorProvider,
                           IAuditService auditService,
                           Optional<AntivirusScanner> antivirusScanner,
                           DocumentStorage storage,
                           FileContentInspector fileInspector,
                           @Value("${app.document-storage.allowed-content-types}") List<String> allowedContentTypes,
                           @Value("${app.document-storage.max-file-size-bytes}") long maxFileSizeBytes,
                           @Value("${app.document-storage.antivirus.enabled:false}") boolean antivirusEnabled,
                           Clock clock,
                           TransactionTemplate transactionTemplate) {
        this.repository = repository;
        this.workflowEngineService = workflowEngineService;
        this.currentActorProvider = currentActorProvider;
        this.auditService = auditService;
        this.antivirusScanner = antivirusScanner;
        this.storage = storage;
        this.fileInspector = fileInspector;
        this.allowedContentTypes = Set.copyOf(allowedContentTypes);
        this.maxFileSizeBytes = maxFileSizeBytes;
        this.antivirusEnabled = antivirusEnabled;
        this.clock = clock;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public DemandDocumentModel upload(Long demandId, UploadedFile file, String documentType) {
        InspectedFile inspected = inspect(demandId, file);
        return transactionTemplate.execute(status -> record(demandId, inspected, documentType));
    }

    @Override
    public InspectedFile inspect(Long demandId, UploadedFile file) {
        DemandModel demand = workflowEngineService.getDemand(demandId);
        byte[] content = file.content();
        if (content.length == 0 || content.length > maxFileSizeBytes) {
            throw WorkflowException.of(FILE_TOO_LARGE);
        }
        String detectedContentType = fileInspector.detectContentType(content);
        if (!allowedContentTypes.contains(detectedContentType)) {
            throw WorkflowException.of(FILE_TYPE_NOT_ALLOWED);
        }
        String checksum = sha256Hex(content);
        ScanStatus scanStatus = scan(demand, file, currentActorProvider.getCurrentUsername(), checksum, content);
        return new InspectedFile(sanitizeFilename(file.originalFilename()), content, detectedContentType, checksum,
            scanStatus);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public DemandDocumentModel record(Long demandId, InspectedFile file, String documentType) {
        DemandModel demand = workflowEngineService.getDemand(demandId);
        DemandStepModel currentStep = workflowEngineService.getCurrentStep(demandId);
        String username = currentActorProvider.getCurrentUsername();

        String storedFilename = UUID.randomUUID() + fileInspector.extensionFor(file.contentType());
        storage.store(demandId, storedFilename, file.content());
        deleteStoredFileOnRollback(demandId, storedFilename);

        DemandDocumentModel toSave = new DemandDocumentModel(null, null, demandId, currentStep.stepCode(),
            currentStep.stepName(), file.originalFilename(), storedFilename, file.contentType(),
            (long) file.content().length, file.checksum(), documentType, DocumentStatus.ACTIVE, file.scanStatus(),
            null, null, username, clock.instant());
        DemandDocumentModel saved = repository.save(toSave);

        if (documentType != null && !documentType.isBlank()) {
            repository.findActiveByType(demandId, documentType)
                .filter(existing -> !existing.id().equals(saved.id()))
                .ifPresent(existing -> repository.supersede(existing.id(), saved.id()));
        }

        auditService.log(username, AuditAction.DEMAND_DOCUMENT_UPLOADED, AuditTarget.demand(demand.publicId()),
            "demandId=" + demandId + ", stepCode=" + currentStep.stepCode() + ", documentId=" + saved.id()
                + ", filename=" + saved.originalFilename() + ", checksum=" + file.checksum());
        LOGGER.info("upload - demandId={}, documentId={}, scanStatus={}", demandId, saved.id(), file.scanStatus());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DemandDocumentModel> list(Long demandId) {
        workflowEngineService.getDemand(demandId);
        return repository.findByDemandId(demandId);
    }

    @Override
    @Transactional(readOnly = true)
    public DownloadableDocument download(Long demandId, Long documentId) {
        DemandModel demand = workflowEngineService.getDemand(demandId);
        DemandDocumentModel document = repository.findById(demandId, documentId)
            .orElseThrow(() -> WorkflowException.of(DOCUMENT_NOT_FOUND));
        if (document.scanStatus() != ScanStatus.CLEAN && document.scanStatus() != ScanStatus.SKIPPED) {
            throw WorkflowException.of(DOCUMENT_NOT_READY);
        }
        auditService.log(currentActorProvider.getCurrentUsername(), AuditAction.DEMAND_DOCUMENT_DOWNLOADED,
            AuditTarget.demand(demand.publicId()),
            "demandId=" + demandId + ", documentId=" + documentId);
        return new DownloadableDocument(document, storage.load(demandId, document.storedFilename()));
    }

    @Override
    @Transactional(readOnly = true)
    public Long resolveDocumentId(UUID publicId) {
        return repository.findIdByPublicId(publicId)
            .orElseThrow(() -> WorkflowException.of(DOCUMENT_NOT_FOUND));
    }

    private ScanStatus scan(DemandModel demand, UploadedFile file, String username, String checksum,
            byte[] content) {
        if (!antivirusEnabled) {
            return ScanStatus.SKIPPED;
        }
        AntivirusScanner scanner = antivirusScanner.orElseThrow(() -> new IllegalStateException(
            "app.document-storage.antivirus.enabled=true but no AntivirusScanner bean is configured"));
        if (scanner.isInfected(content)) {
            auditService.log(username, AuditAction.DEMAND_DOCUMENT_REJECTED_INFECTED,
                AuditTarget.demand(demand.publicId()),
                "demandId=" + demand.id() + ", filename=" + file.originalFilename() + ", checksum=" + checksum);
            throw WorkflowException.of(FILE_INFECTED);
        }
        return ScanStatus.CLEAN;
    }

    private void deleteStoredFileOnRollback(Long demandId, String storedFilename) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    storage.delete(demandId, storedFilename);
                }
            }
        });
    }

    private static String sanitizeFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "document";
        }
        String normalized = originalFilename.replace("\\", "/");
        int lastSlash = normalized.lastIndexOf('/');
        String name = lastSlash >= 0 ? normalized.substring(lastSlash + 1) : normalized;
        return name.length() > 255 ? name.substring(0, 255) : name;
    }

    private static String sha256Hex(byte[] content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(content));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
