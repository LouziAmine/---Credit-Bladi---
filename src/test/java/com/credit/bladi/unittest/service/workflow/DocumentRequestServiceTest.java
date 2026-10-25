package com.credit.bladi.unittest.service.workflow;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1500000;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_INITIATION_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.REFERENCE_DEM_ABCDEF12;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_CONTACT_AT_ATLAS_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_MANAGER1_AT_BLADI_CREDIT_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0612345678;
import static com.credit.bladi.unittest.mock.data.MockFiles.FILE_ID_PDF;
import static com.credit.bladi.unittest.mock.data.MockFiles.FILE_UUID_PDF;
import static com.credit.bladi.unittest.mock.data.MockFiles.MIME_APPLICATION_PDF;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_INITIATION;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_PIECE_D_IDENTITE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDUPONT;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.exception.DocumentRequestException;
import com.credit.bladi.exception.DocumentRequestException.DocumentRequestExceptionType;
import com.credit.bladi.model.audit.AuditTarget;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.DocumentRequestStatus;
import com.credit.bladi.model.enums.DocumentStatus;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.enums.NotificationType;
import com.credit.bladi.model.enums.ScanStatus;
import com.credit.bladi.model.notification.LocalizedText;
import com.credit.bladi.model.notification.NotificationMessage;
import com.credit.bladi.model.user.User;
import com.credit.bladi.model.vo.Money;
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
import com.credit.bladi.service.workflow.document.DocumentRequestService;
import com.credit.bladi.service.workflow.document.IDocumentService;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import com.credit.bladi.unittest.mock.workflow.MockDemand;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@ExtendWith(MockitoExtension.class)
class DocumentRequestServiceTest {
    @Mock
    private IDocumentRequestRepository repository;

    @Mock
    private IWorkflowEngineService workflowEngineService;

    @Mock
    private IDocumentService documentService;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IAuditService auditService;

    @Mock
    private ICurrentActorProvider currentActorProvider;

    @Mock
    private DemandContactNotifier demandContactNotifier;

    @Mock
    private INotificationService notificationService;

    private DocumentRequestService service;

    @BeforeEach
    void setUp() {
        service = new DocumentRequestService(repository, workflowEngineService, documentService, userRepository,
            auditService, currentActorProvider, demandContactNotifier, notificationService,
            new AfterCommitNotifications(Runnable::run), Clock.systemUTC(),
            new TransactionTemplate(mock(PlatformTransactionManager.class)));
    }

    private static DemandModel demand() {
        return MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L, COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, Money.of(AMOUNT_1500000), 84, Instant.now(), null, null,
            EMAIL_CONTACT_AT_ATLAS_MA, PHONE_0612345678, null, null, null, null, 5L, null, null, null);
    }

    private static DocumentRequestModel pendingRequest() {
        return new DocumentRequestModel(1L, UUID.randomUUID(), 1L, null, LABEL_PIECE_D_IDENTITE,
            DocumentRequestStatus.PENDING,
            USER_MANAGER1, Instant.now(), null, null, null);
    }

    private static DocumentRequestModel fulfilledRequest() {
        return new DocumentRequestModel(1L, UUID.randomUUID(), 1L, null, LABEL_PIECE_D_IDENTITE,
            DocumentRequestStatus.FULFILLED, USER_MANAGER1, Instant.now(), 7L, null, Instant.now());
    }

    @Test
    void shouldCreateRequestAndNotifyDemandContact() {
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_MANAGER1);
        when(workflowEngineService.getDemand(1L)).thenReturn(demand());
        when(repository.create(1L, LABEL_PIECE_D_IDENTITE, USER_MANAGER1)).thenReturn(pendingRequest());

        DocumentRequestModel result = service.createRequest(1L, LABEL_PIECE_D_IDENTITE);

        assertEquals(DocumentRequestStatus.PENDING, result.status());
        verify(auditService).log(eq(USER_MANAGER1), eq(AuditAction.DOCUMENT_REQUEST_CREATED), any(AuditTarget.class),
            anyString());
        verify(demandContactNotifier).notify(any(DemandModel.class), eq(NotificationType.DOCUMENT_REQUESTED),
            any(LocalizedText.class), any(LocalizedText.class));
    }

    @Test
    void shouldListForDemand() {
        when(repository.findByDemandId(1L)).thenReturn(List.of(pendingRequest()));

        assertEquals(1, service.listForDemand(1L).size());
    }

    @Test
    void shouldThrowWhenRequestNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        UploadedFile file = new UploadedFile(FILE_ID_PDF, new byte[]{1});

        DocumentRequestException ex = assertThrows(DocumentRequestException.class,
            () -> service.uploadForRequest(1L, 99L, file));

        assertEquals(DocumentRequestExceptionType.REQUEST_NOT_FOUND, ex.getType());
    }

    @Test
    void shouldThrowWhenRequestDoesNotBelongToDemand() {
        when(repository.findById(1L)).thenReturn(Optional.of(pendingRequest()));
        UploadedFile file = new UploadedFile(FILE_ID_PDF, new byte[]{1});

        DocumentRequestException ex = assertThrows(DocumentRequestException.class,
            () -> service.uploadForRequest(2L, 1L, file));

        assertEquals(DocumentRequestExceptionType.REQUEST_NOT_FOR_DEMAND, ex.getType());
    }

    @Test
    void shouldFulfillRequestAndNotifyRequestingManagerWithoutSms() {
        when(repository.findById(1L)).thenReturn(Optional.of(pendingRequest()));
        UploadedFile file = new UploadedFile(FILE_ID_PDF, new byte[]{1});
        DemandDocumentModel uploaded = new DemandDocumentModel(7L, UUID.randomUUID(), 1L, CODE_INITIATION_STEP,
            LABEL_INITIATION, FILE_ID_PDF, FILE_UUID_PDF, MIME_APPLICATION_PDF, 1L, "checksum", LABEL_PIECE_D_IDENTITE,
            DocumentStatus.ACTIVE, ScanStatus.CLEAN, null, null, USER_JDUPONT, Instant.now());
        InspectedFile inspected = new InspectedFile(FILE_ID_PDF, new byte[]{1}, MIME_APPLICATION_PDF, "checksum",
            ScanStatus.CLEAN);
        when(documentService.inspect(1L, file)).thenReturn(inspected);
        when(documentService.record(1L, inspected, LABEL_PIECE_D_IDENTITE)).thenReturn(uploaded);
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_JDUPONT);
        when(workflowEngineService.getDemand(1L)).thenReturn(demand());
        User manager = User.getBuilder().id(2L).username(USER_MANAGER1)
            .email(EMAIL_MANAGER1_AT_BLADI_CREDIT_MA).build();
        when(userRepository.findByUsername(USER_MANAGER1)).thenReturn(Optional.of(manager));

        DemandDocumentModel result = service.uploadForRequest(1L, 1L, file);

        assertEquals(7L, result.id());
        verify(repository).fulfill(eq(1L), eq(7L), any(Instant.class));
        verify(auditService).log(eq(USER_JDUPONT), eq(AuditAction.DOCUMENT_REQUEST_FULFILLED), any(AuditTarget.class),
            anyString());

        ArgumentCaptor<NotificationMessage> captor = ArgumentCaptor.forClass(NotificationMessage.class);
        verify(notificationService).notify(captor.capture());
        assertEquals(NotificationType.DOCUMENT_REQUEST_FULFILLED, captor.getValue().type());
        assertEquals(USER_MANAGER1, captor.getValue().recipientUsername());
        assertEquals(EMAIL_MANAGER1_AT_BLADI_CREDIT_MA, captor.getValue().recipientEmail());
        assertEquals(null, captor.getValue().recipientPhone());
    }

    @Test
    void shouldSkipManagerNotificationWhenManagerNoLongerExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(pendingRequest()));
        UploadedFile file = new UploadedFile(FILE_ID_PDF, new byte[]{1});
        DemandDocumentModel uploaded = new DemandDocumentModel(7L, UUID.randomUUID(), 1L, CODE_INITIATION_STEP,
            LABEL_INITIATION, FILE_ID_PDF, FILE_UUID_PDF, MIME_APPLICATION_PDF, 1L, "checksum", LABEL_PIECE_D_IDENTITE,
            DocumentStatus.ACTIVE, ScanStatus.CLEAN, null, null, USER_JDUPONT, Instant.now());
        InspectedFile inspected = new InspectedFile(FILE_ID_PDF, new byte[]{1}, MIME_APPLICATION_PDF, "checksum",
            ScanStatus.CLEAN);
        when(documentService.inspect(1L, file)).thenReturn(inspected);
        when(documentService.record(1L, inspected, LABEL_PIECE_D_IDENTITE)).thenReturn(uploaded);
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_JDUPONT);
        when(workflowEngineService.getDemand(1L)).thenReturn(demand());
        when(userRepository.findByUsername(USER_MANAGER1)).thenReturn(Optional.empty());

        service.uploadForRequest(1L, 1L, file);

        verify(notificationService, never()).notify(any());
    }

    @Test
    void shouldDownloadTheDocumentThatFulfilledARequest() {
        when(repository.findById(1L)).thenReturn(Optional.of(fulfilledRequest()));
        DemandDocumentModel document = new DemandDocumentModel(7L, UUID.randomUUID(), 1L, CODE_INITIATION_STEP,
            LABEL_INITIATION, FILE_ID_PDF, FILE_UUID_PDF, MIME_APPLICATION_PDF, 1L, "checksum", LABEL_PIECE_D_IDENTITE,
            DocumentStatus.ACTIVE, ScanStatus.CLEAN, null, null, USER_JDUPONT, Instant.now());
        DownloadableDocument downloadable = new DownloadableDocument(document, new byte[]{1});
        when(documentService.download(1L, 7L)).thenReturn(downloadable);

        DownloadableDocument result = service.downloadFulfilledDocument(1L, 1L);

        assertEquals(downloadable, result);
    }

    @Test
    void shouldThrowWhenDownloadingAndRequestNotFulfilledYet() {
        when(repository.findById(1L)).thenReturn(Optional.of(pendingRequest()));

        DocumentRequestException ex = assertThrows(DocumentRequestException.class,
            () -> service.downloadFulfilledDocument(1L, 1L));

        assertEquals(DocumentRequestExceptionType.REQUEST_NOT_FULFILLED, ex.getType());
    }

    @Test
    void shouldThrowWhenDownloadingARequestForAnotherDemand() {
        when(repository.findById(1L)).thenReturn(Optional.of(fulfilledRequest()));

        DocumentRequestException ex = assertThrows(DocumentRequestException.class,
            () -> service.downloadFulfilledDocument(2L, 1L));

        assertEquals(DocumentRequestExceptionType.REQUEST_NOT_FOR_DEMAND, ex.getType());
    }
}
