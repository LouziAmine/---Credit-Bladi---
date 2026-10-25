package com.credit.bladi.unittest.service.enterprise;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_UNKNOWN;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_CONTACT_AT_ATLAS_MA;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_RC_45210;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_TEXTILE;
import static com.credit.bladi.unittest.mock.data.MockTokens.TOKEN_INVALIDE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_GHOST;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDUPONT;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER2;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MDUPONT;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_SARA_BENNANI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.exception.AuthException;
import com.credit.bladi.exception.EnterpriseException;
import com.credit.bladi.exception.EnterpriseUserLinkException;
import com.credit.bladi.exception.EnterpriseUserLinkException.EnterpriseUserLinkExceptionType;
import com.credit.bladi.model.client.EnterpriseModel;
import com.credit.bladi.model.enterprise.EnterpriseUserLinkModel;
import com.credit.bladi.model.enterprise.event.EnterpriseLinkStatusChangedEvent;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.EnterpriseLinkOrigin;
import com.credit.bladi.model.enums.EnterpriseLinkRole;
import com.credit.bladi.model.enums.EnterpriseLinkStatus;
import com.credit.bladi.model.enums.Language;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.user.User;
import com.credit.bladi.repository.client.IEnterpriseRepository;
import com.credit.bladi.repository.enterprise.IEnterpriseUserLinkRepository;
import com.credit.bladi.repository.user.IUserRepository;
import com.credit.bladi.service.enterprise.EnterpriseUserLinkService;
import com.credit.bladi.service.invitation.IClientInvitationService;
import com.credit.bladi.service.security.ICurrentActorProvider;
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
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class EnterpriseUserLinkServiceTest {
    private static final UUID ENTERPRISE_PUBLIC_ID = UUID.randomUUID();

    private static final UUID LINK_PUBLIC_ID = UUID.randomUUID();

    @Mock
    private IEnterpriseUserLinkRepository linkRepository;

    @Mock
    private IEnterpriseRepository enterpriseRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private ICurrentActorProvider currentActorProvider;

    @Mock
    private IClientInvitationService invitationService;

    private EnterpriseUserLinkService service;

    @BeforeEach
    void setUp() {
        service = new EnterpriseUserLinkService(linkRepository, enterpriseRepository, userRepository, eventPublisher,
            currentActorProvider, invitationService, Clock.systemUTC());
    }

    private EnterpriseLinkStatusChangedEvent capturedEvent() {
        ArgumentCaptor<EnterpriseLinkStatusChangedEvent> captor =
            ArgumentCaptor.forClass(EnterpriseLinkStatusChangedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        return captor.getValue();
    }

    private static EnterpriseModel enterprise() {
        return new EnterpriseModel(1L, ENTERPRISE_PUBLIC_ID, COMPANY_ATLAS_TEXTILE_SARL, LABEL_TEXTILE, LABEL_RC_45210,
            null,
            Instant.now());
    }

    private static User user(long id, String username, UserRole role) {
        return User.getBuilder().id(id).username(username).role(role).email(username + "@test.ma").build();
    }

    private static EnterpriseUserLinkModel link(long id, EnterpriseLinkStatus status) {
        return new EnterpriseUserLinkModel(id, LINK_PUBLIC_ID, 1L, null, 5L, null, EnterpriseLinkRole.RESPONSABLE,
            status,
            EnterpriseLinkOrigin.CLIENT_CLAIM, LABEL_RC_45210, USER_JDUPONT, null, null, Instant.now(), null);
    }

    @Test
    void shouldInviteClientAndApproveImmediately() {
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_MANAGER1);
        when(enterpriseRepository.findById(1L)).thenReturn(Optional.of(enterprise()));
        when(userRepository.findByUsername(USER_JDUPONT)).thenReturn(Optional.of(user(5L, USER_JDUPONT,
            UserRole.CLIENT)));
        when(linkRepository.findActiveLink(1L, 5L)).thenReturn(Optional.empty());
        EnterpriseUserLinkModel created = link(10L, EnterpriseLinkStatus.APPROVED);
        when(linkRepository.create(eq(1L), eq(5L), eq(EnterpriseLinkRole.RESPONSABLE),
            eq(EnterpriseLinkStatus.APPROVED), eq(EnterpriseLinkOrigin.MANAGER_INVITE), eq(null), eq(USER_MANAGER1)))
            .thenReturn(created);
        when(linkRepository.updateStatus(eq(10L), eq(EnterpriseLinkStatus.APPROVED), eq(USER_MANAGER1), any()))
            .thenReturn(created);

        EnterpriseUserLinkModel result = service.inviteClient(1L, USER_JDUPONT, EnterpriseLinkRole.RESPONSABLE);

        assertEquals(EnterpriseLinkStatus.APPROVED, result.status());
        EnterpriseLinkStatusChangedEvent event = capturedEvent();
        assertEquals(AuditAction.ENTERPRISE_USER_LINK_INVITED, event.auditAction());
        assertEquals(1, event.notifications().size());
    }

    @Test
    void shouldRejectInviteWhenEnterpriseNotFound() {
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_MANAGER1);
        when(enterpriseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EnterpriseException.class,
            () -> service.inviteClient(99L, USER_JDUPONT, EnterpriseLinkRole.RESPONSABLE));
    }

    @Test
    void shouldRejectInviteWhenTargetUserNotFound() {
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_MANAGER1);
        when(enterpriseRepository.findById(1L)).thenReturn(Optional.of(enterprise()));
        when(userRepository.findByUsername(USER_GHOST)).thenReturn(Optional.empty());

        assertThrows(AuthException.class, () -> service.inviteClient(1L, USER_GHOST, EnterpriseLinkRole.RESPONSABLE));
    }

    @Test
    void shouldRejectInviteWhenTargetIsNotClient() {
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_MANAGER1);
        when(enterpriseRepository.findById(1L)).thenReturn(Optional.of(enterprise()));
        when(userRepository.findByUsername(USER_MANAGER2))
            .thenReturn(Optional.of(user(6L, USER_MANAGER2, UserRole.MANAGER)));

        EnterpriseUserLinkException ex = assertThrows(EnterpriseUserLinkException.class,
            () -> service.inviteClient(1L, USER_MANAGER2, EnterpriseLinkRole.RESPONSABLE));
        assertEquals(EnterpriseUserLinkExceptionType.USER_NOT_CLIENT, ex.getType());
    }

    @Test
    void shouldRejectInviteWhenActiveLinkAlreadyExists() {
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_MANAGER1);
        when(enterpriseRepository.findById(1L)).thenReturn(Optional.of(enterprise()));
        when(userRepository.findByUsername(USER_JDUPONT)).thenReturn(Optional.of(user(5L, USER_JDUPONT,
            UserRole.CLIENT)));
        when(linkRepository.findActiveLink(1L, 5L)).thenReturn(Optional.of(link(3L, EnterpriseLinkStatus.PENDING)));

        EnterpriseUserLinkException ex = assertThrows(EnterpriseUserLinkException.class,
            () -> service.inviteClient(1L, USER_JDUPONT, EnterpriseLinkRole.RESPONSABLE));
        assertEquals(EnterpriseUserLinkExceptionType.LINK_ALREADY_ACTIVE, ex.getType());
        verify(linkRepository, never()).create(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldClaimEnterpriseAsPendingAndNotifyManagers() {
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_JDUPONT);
        when(enterpriseRepository.findByRegistrationNumber(LABEL_RC_45210)).thenReturn(Optional.of(enterprise()));
        when(userRepository.findByUsername(USER_JDUPONT)).thenReturn(Optional.of(user(5L, USER_JDUPONT,
            UserRole.CLIENT)));
        when(linkRepository.findActiveLink(1L, 5L)).thenReturn(Optional.empty());
        EnterpriseUserLinkModel created = link(11L, EnterpriseLinkStatus.PENDING);
        when(linkRepository.create(eq(1L), eq(5L), eq(EnterpriseLinkRole.RESPONSABLE),
            eq(EnterpriseLinkStatus.PENDING), eq(EnterpriseLinkOrigin.CLIENT_CLAIM), eq(LABEL_RC_45210),
            eq(USER_JDUPONT)))
            .thenReturn(created);
        when(userRepository.findAllByRole(UserRole.MANAGER))
            .thenReturn(List.of(user(1L, USER_MANAGER1, UserRole.MANAGER), user(2L, USER_MANAGER2, UserRole.MANAGER)));

        EnterpriseUserLinkModel result = service.claimEnterprise(LABEL_RC_45210, EnterpriseLinkRole.RESPONSABLE);

        assertEquals(EnterpriseLinkStatus.PENDING, result.status());
        EnterpriseLinkStatusChangedEvent event = capturedEvent();
        assertEquals(AuditAction.ENTERPRISE_USER_LINK_CLAIMED, event.auditAction());
        assertEquals(2, event.notifications().size());
    }

    @Test
    void shouldRejectClaimWhenRegistrationNumberUnknown() {
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_JDUPONT);
        when(enterpriseRepository.findByRegistrationNumber(CODE_UNKNOWN)).thenReturn(Optional.empty());

        EnterpriseUserLinkException ex = assertThrows(EnterpriseUserLinkException.class,
            () -> service.claimEnterprise(CODE_UNKNOWN, EnterpriseLinkRole.RESPONSABLE));
        assertEquals(EnterpriseUserLinkExceptionType.REGISTRATION_NUMBER_NOT_FOUND, ex.getType());
    }

    @Test
    void shouldApprovePendingLink() {
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_MANAGER1);
        EnterpriseUserLinkModel pending = link(10L, EnterpriseLinkStatus.PENDING);
        when(linkRepository.findById(10L)).thenReturn(Optional.of(pending));
        when(linkRepository.decideIfPending(eq(10L), eq(EnterpriseLinkStatus.APPROVED), eq(USER_MANAGER1), any()))
            .thenReturn(link(10L, EnterpriseLinkStatus.APPROVED));
        when(userRepository.findById(5L)).thenReturn(Optional.of(user(5L, USER_JDUPONT, UserRole.CLIENT)));

        EnterpriseUserLinkModel result = service.approve(10L);

        assertEquals(EnterpriseLinkStatus.APPROVED, result.status());
        assertEquals(AuditAction.ENTERPRISE_USER_LINK_APPROVED, capturedEvent().auditAction());
    }

    @Test
    void shouldRejectApprovingNonPendingLink() {
        when(linkRepository.findById(10L)).thenReturn(Optional.of(link(10L, EnterpriseLinkStatus.APPROVED)));

        EnterpriseUserLinkException ex = assertThrows(EnterpriseUserLinkException.class, () -> service.approve(10L));
        assertEquals(EnterpriseUserLinkExceptionType.LINK_NOT_PENDING, ex.getType());
    }

    @Test
    void shouldThrowWhenApprovingUnknownLink() {
        when(linkRepository.findById(404L)).thenReturn(Optional.empty());

        EnterpriseUserLinkException ex =
            assertThrows(EnterpriseUserLinkException.class, () -> service.approve(404L));
        assertEquals(EnterpriseUserLinkExceptionType.LINK_NOT_FOUND, ex.getType());
    }

    @Test
    void shouldRejectPendingLinkWithReason() {
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_MANAGER1);
        when(linkRepository.findById(10L)).thenReturn(Optional.of(link(10L, EnterpriseLinkStatus.PENDING)));
        when(linkRepository.decideIfPending(eq(10L), eq(EnterpriseLinkStatus.REJECTED), eq(USER_MANAGER1), any()))
            .thenReturn(link(10L, EnterpriseLinkStatus.REJECTED));
        when(userRepository.findById(5L)).thenReturn(Optional.of(user(5L, USER_JDUPONT, UserRole.CLIENT)));

        EnterpriseUserLinkModel result = service.reject(10L, TOKEN_INVALIDE);

        assertEquals(EnterpriseLinkStatus.REJECTED, result.status());
        assertEquals(AuditAction.ENTERPRISE_USER_LINK_REJECTED, capturedEvent().auditAction());
    }

    @Test
    void shouldRevokeApprovedLink() {
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_MANAGER1);
        when(linkRepository.findById(10L)).thenReturn(Optional.of(link(10L, EnterpriseLinkStatus.APPROVED)));
        when(linkRepository.updateStatus(eq(10L), eq(EnterpriseLinkStatus.REVOKED), eq(USER_MANAGER1), any()))
            .thenReturn(link(10L, EnterpriseLinkStatus.REVOKED));
        when(userRepository.findById(5L)).thenReturn(Optional.of(user(5L, USER_JDUPONT, UserRole.CLIENT)));

        EnterpriseUserLinkModel result = service.revoke(10L);

        assertEquals(EnterpriseLinkStatus.REVOKED, result.status());
        assertEquals(AuditAction.ENTERPRISE_USER_LINK_REVOKED, capturedEvent().auditAction());
    }

    @Test
    void shouldRejectRevokingNonApprovedLink() {
        when(linkRepository.findById(10L)).thenReturn(Optional.of(link(10L, EnterpriseLinkStatus.PENDING)));

        EnterpriseUserLinkException ex = assertThrows(EnterpriseUserLinkException.class, () -> service.revoke(10L));
        assertEquals(EnterpriseUserLinkExceptionType.LINK_NOT_APPROVED, ex.getType());
    }

    @Test
    void shouldThrowWhenCallerHasNoApprovedLinkToEnterprise() {
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_JDUPONT);
        when(userRepository.findByUsername(USER_JDUPONT)).thenReturn(Optional.of(user(5L, USER_JDUPONT,
            UserRole.CLIENT)));
        when(linkRepository.findApprovedByUserId(5L)).thenReturn(List.of());

        EnterpriseUserLinkException ex = assertThrows(EnterpriseUserLinkException.class,
            () -> service.assertCurrentUserApprovedForEnterprise(1L));
        assertEquals(EnterpriseUserLinkExceptionType.ENTERPRISE_NOT_LINKED, ex.getType());
    }

    @Test
    void shouldPassWhenCallerHasApprovedLinkToEnterprise() {
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_JDUPONT);
        when(userRepository.findByUsername(USER_JDUPONT)).thenReturn(Optional.of(user(5L, USER_JDUPONT,
            UserRole.CLIENT)));
        when(linkRepository.findApprovedByUserId(5L)).thenReturn(List.of(link(10L, EnterpriseLinkStatus.APPROVED)));

        service.assertCurrentUserApprovedForEnterprise(1L);
    }

    @Test
    void shouldAutoLinkSingleMatchingClientOnClassification() {
        when(userRepository.findClientsByEmail(EMAIL_CONTACT_AT_ATLAS_MA))
            .thenReturn(List.of(user(5L, USER_JDUPONT, UserRole.CLIENT)));
        when(linkRepository.findActiveLink(1L, 5L)).thenReturn(Optional.empty());
        when(enterpriseRepository.findById(1L)).thenReturn(Optional.of(enterprise()));
        EnterpriseUserLinkModel created = link(30L, EnterpriseLinkStatus.APPROVED);
        when(linkRepository.create(eq(1L), eq(5L), eq(EnterpriseLinkRole.RESPONSABLE),
            eq(EnterpriseLinkStatus.APPROVED), eq(EnterpriseLinkOrigin.AUTO_MATCHED_ON_CLASSIFICATION), eq(null),
            eq(USER_SARA_BENNANI))).thenReturn(created);
        when(linkRepository.updateStatus(eq(30L), eq(EnterpriseLinkStatus.APPROVED), eq(USER_SARA_BENNANI), any()))
            .thenReturn(created);

        service.resolveClientForClassification(1L, EMAIL_CONTACT_AT_ATLAS_MA, Language.FR, 7L, USER_SARA_BENNANI);

        assertEquals(AuditAction.ENTERPRISE_USER_LINK_APPROVED, capturedEvent().auditAction());
        verify(invitationService, never()).createInvitation(any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldSkipAutoLinkWhenClientAlreadyHasActiveLink() {
        when(userRepository.findClientsByEmail(EMAIL_CONTACT_AT_ATLAS_MA))
            .thenReturn(List.of(user(5L, USER_JDUPONT, UserRole.CLIENT)));
        when(linkRepository.findActiveLink(1L, 5L)).thenReturn(Optional.of(link(9L, EnterpriseLinkStatus.APPROVED)));

        service.resolveClientForClassification(1L, EMAIL_CONTACT_AT_ATLAS_MA, Language.FR, 7L, USER_SARA_BENNANI);

        verify(linkRepository, never()).create(any(), any(), any(), any(), any(), any(), any());
        verify(invitationService, never()).createInvitation(any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldCreateInvitationWhenNoClientMatches() {
        when(userRepository.findClientsByEmail(EMAIL_CONTACT_AT_ATLAS_MA)).thenReturn(List.of());

        service.resolveClientForClassification(1L, EMAIL_CONTACT_AT_ATLAS_MA, Language.FR, 7L, USER_SARA_BENNANI);

        verify(invitationService).createInvitation(1L, EMAIL_CONTACT_AT_ATLAS_MA, Language.FR,
            EnterpriseLinkRole.RESPONSABLE, 7L,
            USER_SARA_BENNANI);
        verify(linkRepository, never()).create(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldDoNothingWhenMultipleClientsShareEmail() {
        when(userRepository.findClientsByEmail(EMAIL_CONTACT_AT_ATLAS_MA))
            .thenReturn(List.of(user(5L, USER_JDUPONT, UserRole.CLIENT), user(6L, USER_MDUPONT, UserRole.CLIENT)));

        service.resolveClientForClassification(1L, EMAIL_CONTACT_AT_ATLAS_MA, Language.FR, 7L, USER_SARA_BENNANI);

        verify(linkRepository, never()).create(any(), any(), any(), any(), any(), any(), any());
        verify(invitationService, never()).createInvitation(any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldDoNothingWhenContactEmailBlank() {
        service.resolveClientForClassification(1L, "  ", Language.FR, 7L, USER_SARA_BENNANI);

        verify(userRepository, never()).findClientsByEmail(any());
    }

    @Test
    void shouldListApprovedLinksForCurrentUser() {
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_JDUPONT);
        when(userRepository.findByUsername(USER_JDUPONT)).thenReturn(Optional.of(user(5L, USER_JDUPONT,
            UserRole.CLIENT)));
        when(linkRepository.findApprovedByUserId(5L)).thenReturn(List.of(link(10L, EnterpriseLinkStatus.APPROVED)));

        List<EnterpriseUserLinkModel> result = service.listMyApprovedLinks();

        assertEquals(1, result.size());
        assertEquals(EnterpriseLinkStatus.APPROVED, result.get(0).status());
    }

    @Test
    void shouldListApprovedLinksForEnterprise() {
        when(linkRepository.findApprovedByEnterpriseId(1L))
            .thenReturn(List.of(link(1L, EnterpriseLinkStatus.APPROVED)));

        List<EnterpriseUserLinkModel> result = service.listApprovedForEnterprise(1L);

        assertEquals(1, result.size());
        assertEquals(EnterpriseLinkStatus.APPROVED, result.get(0).status());
    }
}
