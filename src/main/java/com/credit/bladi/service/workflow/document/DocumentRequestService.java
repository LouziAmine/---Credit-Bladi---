package com.credit.bladi.service.workflow.document;

import static com.credit.bladi.exception.DocumentRequestException.DocumentRequestExceptionType.REQUEST_NOT_FOR_DEMAND;
import static com.credit.bladi.exception.DocumentRequestException.DocumentRequestExceptionType.REQUEST_NOT_FOUND;
import static com.credit.bladi.exception.DocumentRequestException.DocumentRequestExceptionType.REQUEST_NOT_FULFILLED;

import com.credit.bladi.exception.DocumentRequestException;
import com.credit.bladi.model.audit.AuditTarget;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.NotificationType;
import com.credit.bladi.model.notification.LocalizedText;
import com.credit.bladi.model.notification.NotificationMessage;
import com.credit.bladi.model.workflow.DemandDocumentModel;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.DocumentRequestModel;
import com.credit.bladi.model.workflow.DownloadableDocument;
import com.credit.bladi.model.workflow.InspectedFile;
import com.credit.bladi.model.workflow.UploadedFile;
import com.credit.bladi.repository.user.IUserRepository;
import com.credit.bladi.repository.workflow.IDocumentRequestRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.notification.AfterCommitNotifications;
import com.credit.bladi.service.notification.DemandContactNotifier;
import com.credit.bladi.service.notification.INotificationService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import java.time.Clock;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class DocumentRequestService implements IDocumentRequestService {
    private final IDocumentRequestRepository repository;

    private final IWorkflowEngineService workflowEngineService;

    private final IDocumentService documentService;

    private final IUserRepository userRepository;

    private final IAuditService auditService;

    private final ICurrentActorProvider currentActorProvider;

    private final DemandContactNotifier demandContactNotifier;

    private final INotificationService notificationService;

    private final AfterCommitNotifications afterCommit;

    private final Clock clock;

    private final TransactionTemplate transactionTemplate;

    public DocumentRequestService(IDocumentRequestRepository repository,
            IWorkflowEngineService workflowEngineService, IDocumentService documentService,
            IUserRepository userRepository, IAuditService auditService,
            ICurrentActorProvider currentActorProvider, DemandContactNotifier demandContactNotifier,
            INotificationService notificationService, AfterCommitNotifications afterCommit, Clock clock,
            TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
        this.repository = repository;
        this.workflowEngineService = workflowEngineService;
        this.documentService = documentService;
        this.userRepository = userRepository;
        this.auditService = auditService;
        this.currentActorProvider = currentActorProvider;
        this.demandContactNotifier = demandContactNotifier;
        this.notificationService = notificationService;
        this.afterCommit = afterCommit;
        this.clock = clock;
    }

    @Override
    @Transactional
    public DocumentRequestModel createRequest(Long demandId, String documentType) {
        String actorUsername = currentActorProvider.getCurrentUsername();
        DemandModel demand = workflowEngineService.getDemand(demandId);
        DocumentRequestModel created = repository.create(demandId, documentType, actorUsername);

        LocalizedText title = LocalizedText.of("documentRequested.title");
        LocalizedText body = LocalizedText.of("documentRequested.body", demand.reference(), documentType);
        afterCommit.send(() -> demandContactNotifier.notify(demand, NotificationType.DOCUMENT_REQUESTED, title, body));

        auditService.log(actorUsername, AuditAction.DOCUMENT_REQUEST_CREATED, AuditTarget.demand(demand.publicId()),
            "demandId=" + demandId + ", requestId=" + created.id() + ", documentType=" + documentType);

        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public Long resolveRequestId(UUID publicId) {
        return repository.findIdByPublicId(publicId)
            .orElseThrow(() -> DocumentRequestException.of(REQUEST_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentRequestModel> listForDemand(Long demandId) {
        return repository.findByDemandId(demandId);
    }

    @Override
    public DemandDocumentModel uploadForRequest(Long demandId, Long requestId, UploadedFile file) {
        DocumentRequestModel request = requireRequestForDemand(demandId, requestId);
        InspectedFile inspected = documentService.inspect(demandId, file);
        return transactionTemplate.execute(status -> fulfill(demandId, request, inspected));
    }

    private DemandDocumentModel fulfill(Long demandId, DocumentRequestModel request, InspectedFile inspected) {
        Long requestId = request.id();
        DemandDocumentModel uploaded = documentService.record(demandId, inspected, request.documentType());
        repository.fulfill(requestId, uploaded.id(), clock.instant());

        DemandModel demand = workflowEngineService.getDemand(demandId);
        userRepository.findByUsername(request.requestedByUsername()).ifPresent(manager -> {
            NotificationMessage notification = new NotificationMessage(manager.getUsername(), manager.getEmail(),
                null, manager.getPreferredLanguage(), NotificationType.DOCUMENT_REQUEST_FULFILLED,
                LocalizedText.of("documentReceived.title"),
                LocalizedText.of("documentReceived.body", request.documentType(), demand.reference()),
                "/admin/demands/" + demand.publicId());
            afterCommit.send(() -> notificationService.notify(notification));
        });

        auditService.log(currentActorProvider.getCurrentUsername(), AuditAction.DOCUMENT_REQUEST_FULFILLED,
            AuditTarget.demand(demand.publicId()),
            "demandId=" + demandId + ", requestId=" + requestId + ", documentId=" + uploaded.id());

        return uploaded;
    }

    @Override
    @Transactional(readOnly = true)
    public DownloadableDocument downloadFulfilledDocument(Long demandId, Long requestId) {
        DocumentRequestModel request = requireRequestForDemand(demandId, requestId);
        if (request.fulfilledDocumentId() == null) {
            throw DocumentRequestException.of(REQUEST_NOT_FULFILLED);
        }
        return documentService.download(demandId, request.fulfilledDocumentId());
    }

    private DocumentRequestModel requireRequestForDemand(Long demandId, Long requestId) {
        DocumentRequestModel request = repository.findById(requestId)
            .orElseThrow(() -> DocumentRequestException.of(REQUEST_NOT_FOUND));
        if (!request.demandId().equals(demandId)) {
            throw DocumentRequestException.of(REQUEST_NOT_FOR_DEMAND);
        }
        return request;
    }
}
