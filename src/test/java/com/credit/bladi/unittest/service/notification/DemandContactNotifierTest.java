package com.credit.bladi.unittest.service.notification;

import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_CONTACT_AT_ATLAS_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_JDUPONT_AT_EXAMPLE_COM;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_MDUPONT_AT_EXAMPLE_COM;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0612345678;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DEM_1;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_TEXTILE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDUPONT;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MDUPONT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.model.client.EnterpriseModel;
import com.credit.bladi.model.enterprise.EnterpriseUserLinkModel;
import com.credit.bladi.model.enums.EnterpriseLinkOrigin;
import com.credit.bladi.model.enums.EnterpriseLinkRole;
import com.credit.bladi.model.enums.EnterpriseLinkStatus;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.enums.Language;
import com.credit.bladi.model.enums.NotificationType;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.notification.LocalizedText;
import com.credit.bladi.model.notification.NotificationMessage;
import com.credit.bladi.model.user.User;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.repository.client.IEnterpriseRepository;
import com.credit.bladi.repository.user.IUserRepository;
import com.credit.bladi.service.enterprise.IApprovedEnterpriseLinksProvider;
import com.credit.bladi.service.notification.DemandContactNotifier;
import com.credit.bladi.service.notification.INotificationService;
import com.credit.bladi.unittest.mock.workflow.MockDemand;
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

@ExtendWith(MockitoExtension.class)
class DemandContactNotifierTest {
    private static final UUID ENTERPRISE_PUBLIC_ID = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");

    private static final UUID DEMAND_PUBLIC_ID = UUID.fromString("9b2e1f0a-1111-4222-8333-444455556666");

    private static final LocalizedText TITLE = LocalizedText.of("title");

    private static final LocalizedText BODY = LocalizedText.of("body");

    @Mock
    private IApprovedEnterpriseLinksProvider enterpriseUserLinkService;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IEnterpriseRepository enterpriseRepository;

    @Mock
    private INotificationService notificationService;

    private DemandContactNotifier notifier;

    @BeforeEach
    void setUp() {
        notifier = new DemandContactNotifier(enterpriseUserLinkService, userRepository, enterpriseRepository,
            notificationService);
    }

    private static DemandModel demand(Long enterpriseId) {
        return MockDemand.enterprise(1L, DEMAND_PUBLIC_ID, LABEL_DEM_1, 1L, COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME,
            null, 84, Instant.now(), null, null, EMAIL_CONTACT_AT_ATLAS_MA, PHONE_0612345678, null, null, null, null,
            enterpriseId, null, LABEL_TEXTILE, 5);
    }

    private static EnterpriseUserLinkModel approvedLink(long userId) {
        return new EnterpriseUserLinkModel(1L, UUID.randomUUID(), 5L, null, userId, null,
            EnterpriseLinkRole.RESPONSABLE,
            EnterpriseLinkStatus.APPROVED, EnterpriseLinkOrigin.MANAGER_INVITE, null, USER_MANAGER1, USER_MANAGER1,
            Instant.now(), Instant.now(), null);
    }

    private static User client(long id, String username, String email, Language language) {
        return User.getBuilder().id(id).username(username).role(UserRole.CLIENT).email(email)
            .preferredLanguage(language).build();
    }

    @Test
    void shouldNotifyRawDemandContactWhenNoClientIsLinked() {
        when(enterpriseUserLinkService.listApprovedForEnterprise(5L)).thenReturn(List.of());
        when(enterpriseRepository.findById(5L)).thenReturn(Optional.of(new EnterpriseModel(5L, ENTERPRISE_PUBLIC_ID,
            COMPANY_ATLAS_TEXTILE_SARL, LABEL_TEXTILE, "RC-1", null, Instant.now())));

        notifier.notify(demand(5L), NotificationType.DEMAND_ENTERED_PIPELINE, TITLE, BODY);

        ArgumentCaptor<NotificationMessage> captor = ArgumentCaptor.forClass(NotificationMessage.class);
        verify(notificationService).notify(captor.capture());
        assertNull(captor.getValue().recipientUsername());
        assertEquals(EMAIL_CONTACT_AT_ATLAS_MA, captor.getValue().recipientEmail());
        assertEquals(PHONE_0612345678, captor.getValue().recipientPhone());
        assertEquals("/portal/" + ENTERPRISE_PUBLIC_ID + "/demands/" + DEMAND_PUBLIC_ID, captor.getValue().link());
    }

    @Test
    void shouldNotifyRawDemandContactWithPortalHomeLinkWhenEnterpriseIdIsNull() {
        notifier.notify(demand(null), NotificationType.DEMAND_ENTERED_PIPELINE, TITLE, BODY);

        ArgumentCaptor<NotificationMessage> captor = ArgumentCaptor.forClass(NotificationMessage.class);
        verify(notificationService).notify(captor.capture());
        assertNull(captor.getValue().recipientUsername());
        assertEquals("/portal", captor.getValue().link());
        verify(enterpriseUserLinkService, never()).listApprovedForEnterprise(any());
    }

    @Test
    void shouldNotifyEachApprovedLinkedClientOnTheirOwnAccountInTheirLanguage() {
        when(enterpriseUserLinkService.listApprovedForEnterprise(5L))
            .thenReturn(List.of(approvedLink(8L), approvedLink(9L)));
        when(userRepository.findAllById(List.of(8L, 9L))).thenReturn(List.of(
            client(8L, USER_JDUPONT, EMAIL_JDUPONT_AT_EXAMPLE_COM, Language.AR),
            client(9L, USER_MDUPONT, EMAIL_MDUPONT_AT_EXAMPLE_COM, null)));

        notifier.notify(demand(5L), NotificationType.DEMAND_COMPLETED, TITLE, BODY);

        ArgumentCaptor<NotificationMessage> captor = ArgumentCaptor.forClass(NotificationMessage.class);
        verify(notificationService, times(2)).notify(captor.capture());
        List<NotificationMessage> messages = captor.getAllValues();
        assertEquals(USER_JDUPONT, messages.get(0).recipientUsername());
        assertEquals(EMAIL_JDUPONT_AT_EXAMPLE_COM, messages.get(0).recipientEmail());
        assertEquals(PHONE_0612345678, messages.get(0).recipientPhone());
        assertEquals(Language.AR, messages.get(0).recipientLanguage());
        assertEquals(USER_MDUPONT, messages.get(1).recipientUsername());
        assertNull(messages.get(1).recipientLanguage());
    }

    @Test
    void shouldSkipLinkedUserThatNoLongerExists() {
        when(enterpriseUserLinkService.listApprovedForEnterprise(5L)).thenReturn(List.of(approvedLink(8L)));
        when(userRepository.findAllById(List.of(8L))).thenReturn(List.of());

        notifier.notify(demand(5L), NotificationType.DEMAND_COMPLETED, TITLE, BODY);

        verify(notificationService, never()).notify(any());
    }
}
