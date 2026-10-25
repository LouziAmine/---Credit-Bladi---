package com.credit.bladi.unittest.service.invitation;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_20000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_600000;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_BORROWER_AT_GMAIL_COM;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_DRCONTABLE_AT_CGI_COM;
import static com.credit.bladi.unittest.mock.data.MockContacts.PERSON_SALMA_IDRISSI;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DEM_MORTGAGE7;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_RC_45210;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_TEXTILE;
import static com.credit.bladi.unittest.mock.data.MockTokens.TOKEN_HASH;
import static com.credit.bladi.unittest.mock.data.MockTokens.TOKEN_HASHED;
import static com.credit.bladi.unittest.mock.data.MockTokens.TOKEN_RAW_TOKEN;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDUPONT;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_SALMA_IDRISSI;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_SARA_BENNANI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.exception.AuthException;
import com.credit.bladi.exception.ClientInvitationException;
import com.credit.bladi.exception.ClientInvitationException.ClientInvitationExceptionType;
import com.credit.bladi.model.client.EnterpriseModel;
import com.credit.bladi.model.enterprise.EnterpriseUserLinkModel;
import com.credit.bladi.model.enums.BorrowerLinkOrigin;
import com.credit.bladi.model.enums.ClientInvitationStatus;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.EnterpriseLinkOrigin;
import com.credit.bladi.model.enums.EnterpriseLinkRole;
import com.credit.bladi.model.enums.EnterpriseLinkStatus;
import com.credit.bladi.model.enums.Language;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.invitation.ClientInvitationModel;
import com.credit.bladi.model.invitation.ClientInvitationPreview;
import com.credit.bladi.model.user.User;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.repository.auth.TokenHasher;
import com.credit.bladi.repository.borrower.IMortgageBorrowerRepository;
import com.credit.bladi.repository.client.IEnterpriseRepository;
import com.credit.bladi.repository.enterprise.IEnterpriseUserLinkRepository;
import com.credit.bladi.repository.invitation.IClientInvitationRepository;
import com.credit.bladi.repository.user.IUserRepository;
import com.credit.bladi.repository.workflow.IWorkflowRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.invitation.ClientInvitationService;
import com.credit.bladi.service.invitation.IInvitationMailer;
import com.credit.bladi.service.notification.AfterCommitNotifications;
import com.credit.bladi.service.request.IRequestContext;
import com.credit.bladi.unittest.mock.workflow.MockDemand;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class ClientInvitationServiceTest {
    @Mock
    private IClientInvitationRepository invitationRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IEnterpriseRepository enterpriseRepository;

    @Mock
    private IEnterpriseUserLinkRepository linkRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IAuditService auditService;

    @Mock
    private IInvitationMailer mailer;

    @Mock
    private IRequestContext requestContext;

    @Mock
    private IMortgageBorrowerRepository borrowerRepository;

    @Mock
    private IWorkflowRepository workflowRepository;

    private ClientInvitationService service;

    @BeforeEach
    void setUp() {
        service = new ClientInvitationService(invitationRepository, userRepository, enterpriseRepository,
            linkRepository, borrowerRepository, workflowRepository, passwordEncoder, auditService, mailer,
            new AfterCommitNotifications(Runnable::run), Clock.systemUTC(), requestContext);
    }

    private static EnterpriseModel enterprise() {
        return new EnterpriseModel(1L, UUID.randomUUID(), COMPANY_ATLAS_TEXTILE_SARL, LABEL_TEXTILE, LABEL_RC_45210,
            null,
            Instant.now());
    }

    private static ClientInvitationModel invitation(ClientInvitationStatus status, Instant expiresAt) {
        return new ClientInvitationModel(10L, EMAIL_DRCONTABLE_AT_CGI_COM, TOKEN_HASH, 1L,
            EnterpriseLinkRole.RESPONSABLE, 5L,
            status, USER_SARA_BENNANI, Instant.now(), expiresAt, null);
    }

    @Test
    void shouldCreateInvitationAndSendMail() {
        when(invitationRepository.findActivePending(EMAIL_DRCONTABLE_AT_CGI_COM, 1L)).thenReturn(Optional.empty());
        when(enterpriseRepository.findById(1L)).thenReturn(Optional.of(enterprise()));
        when(invitationRepository.create(eq(EMAIL_DRCONTABLE_AT_CGI_COM), anyString(), eq(1L),
            eq(EnterpriseLinkRole.RESPONSABLE), eq(5L), eq(USER_SARA_BENNANI), any()))
            .thenReturn(invitation(ClientInvitationStatus.PENDING, Instant.now().plusSeconds(60)));

        service.createInvitation(1L, EMAIL_DRCONTABLE_AT_CGI_COM, Language.FR, EnterpriseLinkRole.RESPONSABLE, 5L,
            USER_SARA_BENNANI);

        verify(mailer).sendInvitation(eq(EMAIL_DRCONTABLE_AT_CGI_COM), eq(COMPANY_ATLAS_TEXTILE_SARL), anyString(),
            eq(Language.FR));
    }

    @Test
    void shouldSkipCreatingInvitationWhenOneAlreadyPending() {
        when(invitationRepository.findActivePending(EMAIL_DRCONTABLE_AT_CGI_COM, 1L))
            .thenReturn(Optional.of(invitation(ClientInvitationStatus.PENDING, Instant.now().plusSeconds(60))));

        service.createInvitation(1L, EMAIL_DRCONTABLE_AT_CGI_COM, Language.FR, EnterpriseLinkRole.RESPONSABLE, 5L,
            USER_SARA_BENNANI);

        verify(invitationRepository, never()).create(any(), any(), any(), any(), any(), any(), any());
        verify(mailer, never()).sendInvitation(any(), any(), any(), any());
    }

    @Test
    void shouldPreviewPendingNonExpiredInvitation() {
        when(invitationRepository.findByTokenHash(anyString()))
            .thenReturn(Optional.of(invitation(ClientInvitationStatus.PENDING, Instant.now().plusSeconds(60))));
        when(enterpriseRepository.findById(1L)).thenReturn(Optional.of(enterprise()));

        ClientInvitationPreview preview = service.preview(TOKEN_RAW_TOKEN);

        assertEquals(EMAIL_DRCONTABLE_AT_CGI_COM, preview.email());
        assertEquals(COMPANY_ATLAS_TEXTILE_SARL, preview.enterpriseName());
    }

    @Test
    void shouldThrowWhenTokenUnknown() {
        when(invitationRepository.findByTokenHash(anyString())).thenReturn(Optional.empty());

        ClientInvitationException ex = assertThrows(ClientInvitationException.class, () -> service.preview("bad"));
        assertEquals(ClientInvitationExceptionType.TOKEN_NOT_FOUND, ex.getType());
    }

    @Test
    void shouldThrowWhenInvitationExpired() {
        when(invitationRepository.findByTokenHash(anyString()))
            .thenReturn(Optional.of(invitation(ClientInvitationStatus.PENDING, Instant.now().minusSeconds(60))));

        ClientInvitationException ex = assertThrows(ClientInvitationException.class,
            () -> service.preview(TOKEN_RAW_TOKEN));
        assertEquals(ClientInvitationExceptionType.TOKEN_EXPIRED, ex.getType());
    }

    @Test
    void shouldAcceptInvitationAndCreateApprovedLink() {
        ClientInvitationModel pending = invitation(ClientInvitationStatus.PENDING, Instant.now().plusSeconds(60));
        when(invitationRepository.findByTokenHash(anyString())).thenReturn(Optional.of(pending));
        when(userRepository.existsByUsername(USER_JDUPONT)).thenReturn(false);
        when(passwordEncoder.encode("secureP@ss1234")).thenReturn(TOKEN_HASHED);
        User saved = User.getBuilder().id(5L).username(USER_JDUPONT).role(UserRole.CLIENT).build();
        when(userRepository.save(any())).thenReturn(saved);
        EnterpriseUserLinkModel link = new EnterpriseUserLinkModel(20L, UUID.randomUUID(), 1L, null, 5L, null,
            EnterpriseLinkRole.RESPONSABLE, EnterpriseLinkStatus.APPROVED, EnterpriseLinkOrigin.EMAIL_INVITATION,
            null, USER_JDUPONT, null, null, Instant.now(), null);
        when(linkRepository.create(eq(1L), eq(5L), eq(EnterpriseLinkRole.RESPONSABLE),
            eq(EnterpriseLinkStatus.APPROVED), eq(EnterpriseLinkOrigin.EMAIL_INVITATION), eq(null), eq(USER_JDUPONT)))
            .thenReturn(link);

        service.accept(TOKEN_RAW_TOKEN, USER_JDUPONT, "secureP@ss1234");

        verify(invitationRepository).markAccepted(eq(10L), any());
        verify(linkRepository).updateStatus(eq(20L), eq(EnterpriseLinkStatus.APPROVED), eq(USER_SARA_BENNANI), any());
        verify(auditService, times(2)).log(anyString(), any(), anyString());
    }

    @Test
    void shouldRejectAcceptWhenUsernameTaken() {
        ClientInvitationModel pending = invitation(ClientInvitationStatus.PENDING, Instant.now().plusSeconds(60));
        when(invitationRepository.findByTokenHash(anyString())).thenReturn(Optional.of(pending));
        when(userRepository.existsByUsername(USER_JDUPONT)).thenReturn(true);

        assertThrows(AuthException.class, () -> service.accept(TOKEN_RAW_TOKEN, USER_JDUPONT, "secureP@ss1234"));
        verify(linkRepository, never()).create(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void tokenHashingShouldBeDeterministicAndStable() {
        assertEquals(TokenHasher.hash("abc"), TokenHasher.hash("abc"));
    }

    private static ClientInvitationModel borrowerInvitation() {
        return new ClientInvitationModel(11L, EMAIL_BORROWER_AT_GMAIL_COM, TOKEN_HASH, null, null, 7L,
            ClientInvitationStatus.PENDING, USER_SARA_BENNANI, Instant.now(), Instant.now().plusSeconds(60), null);
    }

    private static DemandModel mortgageDemand() {
        return MockDemand.mortgage(7L, UUID.randomUUID(), LABEL_DEM_MORTGAGE7, 2L, PERSON_SALMA_IDRISSI,
            Money.of(AMOUNT_500000), 240, Instant.now(), null, null, EMAIL_BORROWER_AT_GMAIL_COM, null, null,
            ProductType.CONVENTIONAL, CspCode.SALA, Money.of(AMOUNT_600000),
            Money.of(AMOUNT_20000), 35, false, null);
    }

    @Test
    void shouldInviteAMortgageBorrowerToTheirDossierWithoutAnyEnterprise() {
        when(invitationRepository.findActivePendingForDemand(EMAIL_BORROWER_AT_GMAIL_COM,
            7L)).thenReturn(Optional.empty());
        when(workflowRepository.findDemandById(7L)).thenReturn(Optional.of(mortgageDemand()));
        when(invitationRepository.create(eq(EMAIL_BORROWER_AT_GMAIL_COM), anyString(), isNull(), isNull(), eq(7L),
            eq(USER_SARA_BENNANI), any())).thenReturn(borrowerInvitation());

        service.createBorrowerInvitation(7L, EMAIL_BORROWER_AT_GMAIL_COM, Language.FR, USER_SARA_BENNANI);

        verify(mailer).sendBorrowerInvitation(eq(EMAIL_BORROWER_AT_GMAIL_COM), eq(LABEL_DEM_MORTGAGE7), anyString(),
            eq(Language.FR));
        verify(mailer, never()).sendInvitation(any(), any(), any(), any());
    }

    @Test
    void shouldNotInviteTheSameBorrowerTwiceForOneDossier() {
        when(invitationRepository.findActivePendingForDemand(EMAIL_BORROWER_AT_GMAIL_COM, 7L))
            .thenReturn(Optional.of(borrowerInvitation()));

        service.createBorrowerInvitation(7L, EMAIL_BORROWER_AT_GMAIL_COM, Language.FR, USER_SARA_BENNANI);

        verify(invitationRepository, never()).create(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldPreviewABorrowerInvitationWithTheDossierReference() {
        when(invitationRepository.findByTokenHash(anyString())).thenReturn(Optional.of(borrowerInvitation()));
        when(workflowRepository.findDemandById(7L)).thenReturn(Optional.of(mortgageDemand()));

        ClientInvitationPreview preview = service.preview(TOKEN_RAW_TOKEN);

        assertEquals(LABEL_DEM_MORTGAGE7, preview.demandReference());
        assertNull(preview.enterpriseName());
    }

    @Test
    void shouldMakeTheNewAccountTheBorrowerOfTheDossierOnAccept() {
        when(invitationRepository.findByTokenHash(anyString())).thenReturn(Optional.of(borrowerInvitation()));
        when(userRepository.existsByUsername(USER_SALMA_IDRISSI)).thenReturn(false);
        when(passwordEncoder.encode("secureP@ss1234")).thenReturn(TOKEN_HASHED);
        when(userRepository.save(any())).thenReturn(
            User.getBuilder().id(9L).username(USER_SALMA_IDRISSI).role(UserRole.CLIENT).build());
        when(workflowRepository.findDemandById(7L)).thenReturn(Optional.of(mortgageDemand()));

        service.accept(TOKEN_RAW_TOKEN, USER_SALMA_IDRISSI, "secureP@ss1234");

        verify(invitationRepository).markAccepted(eq(11L), any());
        verify(borrowerRepository).link(7L, 9L, BorrowerLinkOrigin.EMAIL_INVITATION, USER_SARA_BENNANI);
        verify(linkRepository, never()).create(any(), any(), any(), any(), any(), any(), any());
        verify(mailer).sendBorrowerAccountConfirmation(eq(EMAIL_BORROWER_AT_GMAIL_COM), eq(USER_SALMA_IDRISSI),
            eq(LABEL_DEM_MORTGAGE7), any());
    }
}
