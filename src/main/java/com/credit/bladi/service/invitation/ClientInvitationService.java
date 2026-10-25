package com.credit.bladi.service.invitation;

import static com.credit.bladi.common.PiiMasker.maskEmail;
import static com.credit.bladi.exception.AuthException.AuthExceptionType.USERNAME_TAKEN;
import static com.credit.bladi.exception.ClientInvitationException.ClientInvitationExceptionType.TOKEN_EXPIRED;
import static com.credit.bladi.exception.ClientInvitationException.ClientInvitationExceptionType.TOKEN_NOT_FOUND;
import static com.credit.bladi.exception.EnterpriseException.EnterpriseExceptionType.ENTERPRISE_NOT_FOUND;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.DEMAND_NOT_FOUND;

import com.credit.bladi.exception.AuthException;
import com.credit.bladi.exception.ClientInvitationException;
import com.credit.bladi.exception.EnterpriseException;
import com.credit.bladi.exception.WorkflowException;
import com.credit.bladi.model.client.EnterpriseModel;
import com.credit.bladi.model.enterprise.EnterpriseUserLinkModel;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.BorrowerLinkOrigin;
import com.credit.bladi.model.enums.ClientInvitationStatus;
import com.credit.bladi.model.enums.EnterpriseLinkOrigin;
import com.credit.bladi.model.enums.EnterpriseLinkRole;
import com.credit.bladi.model.enums.EnterpriseLinkStatus;
import com.credit.bladi.model.enums.Language;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.invitation.ClientInvitationModel;
import com.credit.bladi.model.invitation.ClientInvitationPreview;
import com.credit.bladi.model.user.User;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.repository.DuplicateEntryException;
import com.credit.bladi.repository.auth.TokenHasher;
import com.credit.bladi.repository.borrower.IMortgageBorrowerRepository;
import com.credit.bladi.repository.client.IEnterpriseRepository;
import com.credit.bladi.repository.enterprise.IEnterpriseUserLinkRepository;
import com.credit.bladi.repository.invitation.IClientInvitationRepository;
import com.credit.bladi.repository.user.IUserRepository;
import com.credit.bladi.repository.workflow.IWorkflowRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.notification.AfterCommitNotifications;
import com.credit.bladi.service.request.IRequestContext;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
public class ClientInvitationService implements IClientInvitationService {
    private static final Logger LOGGER = LogManager.getLogger(ClientInvitationService.class);

    private static final Duration INVITATION_VALIDITY = Duration.ofDays(7);

    private final IClientInvitationRepository invitationRepository;

    private final IUserRepository userRepository;

    private final IEnterpriseRepository enterpriseRepository;

    private final IEnterpriseUserLinkRepository linkRepository;

    private final IMortgageBorrowerRepository borrowerRepository;

    private final IWorkflowRepository workflowRepository;

    private final PasswordEncoder passwordEncoder;

    private final IAuditService auditService;

    private final IInvitationMailer mailer;

    private final AfterCommitNotifications afterCommit;

    private final Clock clock;

    private final IRequestContext requestContext;

    public ClientInvitationService(IClientInvitationRepository invitationRepository, IUserRepository userRepository,
            IEnterpriseRepository enterpriseRepository, IEnterpriseUserLinkRepository linkRepository,
            IMortgageBorrowerRepository borrowerRepository, IWorkflowRepository workflowRepository,
            PasswordEncoder passwordEncoder, IAuditService auditService, IInvitationMailer mailer,
            AfterCommitNotifications afterCommit, Clock clock, IRequestContext requestContext) {
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.linkRepository = linkRepository;
        this.borrowerRepository = borrowerRepository;
        this.workflowRepository = workflowRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
        this.mailer = mailer;
        this.afterCommit = afterCommit;
        this.clock = clock;
        this.requestContext = requestContext;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createInvitation(Long enterpriseId, String email, Language language, EnterpriseLinkRole role,
            Long demandId, String createdByUsername) {
        if (invitationRepository.findActivePending(email, enterpriseId).isPresent()) {
            LOGGER.debug("CLIENT_INVITATION_SKIPPED_ALREADY_PENDING email={}, enterpriseId={}", maskEmail(email),
                enterpriseId);
            return;
        }
        EnterpriseModel enterprise = enterpriseRepository.findById(enterpriseId)
            .orElseThrow(() -> EnterpriseException.of(ENTERPRISE_NOT_FOUND));

        String rawToken = UUID.randomUUID().toString();
        String tokenHash = TokenHasher.hash(rawToken);
        Instant expiresAt = clock.instant().plus(INVITATION_VALIDITY);
        ClientInvitationModel invitation;
        try {
            invitation = invitationRepository.create(email, tokenHash, enterpriseId, role, demandId,
                createdByUsername, expiresAt);
        } catch (DuplicateEntryException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOGGER.debug("CLIENT_INVITATION_SKIPPED_ALREADY_PENDING email={}, enterpriseId={}", maskEmail(email),
                enterpriseId);
            return;
        }

        afterCommit.send(() -> mailer.sendInvitation(email, enterprise.name(), rawToken, language));
        auditService.log(createdByUsername, AuditAction.CLIENT_INVITATION_CREATED,
            "invitationId=" + invitation.id() + ", enterpriseId=" + enterpriseId + ", email=" + email);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createBorrowerInvitation(Long demandId, String email, Language language, String createdByUsername) {
        if (invitationRepository.findActivePendingForDemand(email, demandId).isPresent()) {
            LOGGER.debug("BORROWER_INVITATION_SKIPPED_ALREADY_PENDING email={}, demandId={}", maskEmail(email),
                demandId);
            return;
        }
        String reference = requireDemand(demandId).reference();
        String rawToken = UUID.randomUUID().toString();
        ClientInvitationModel invitation;
        try {
            invitation = invitationRepository.create(email, TokenHasher.hash(rawToken), null, null, demandId,
                createdByUsername, clock.instant().plus(INVITATION_VALIDITY));
        } catch (DuplicateEntryException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOGGER.debug("BORROWER_INVITATION_SKIPPED_ALREADY_PENDING email={}, demandId={}", maskEmail(email),
                demandId);
            return;
        }

        afterCommit.send(() -> mailer.sendBorrowerInvitation(email, reference, rawToken, language));
        auditService.log(createdByUsername, AuditAction.CLIENT_INVITATION_CREATED,
            "invitationId=" + invitation.id() + ", demandId=" + demandId + ", email=" + email);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientInvitationPreview preview(String rawToken) {
        ClientInvitationModel invitation = requirePendingInvitation(rawToken);
        if (isBorrowerInvitation(invitation)) {
            return new ClientInvitationPreview(invitation.email(), null,
                requireDemand(invitation.demandId()).reference(), invitation.expiresAt());
        }
        EnterpriseModel enterprise = enterpriseRepository.findById(invitation.enterpriseId())
            .orElseThrow(() -> EnterpriseException.of(ENTERPRISE_NOT_FOUND));
        return new ClientInvitationPreview(invitation.email(), enterprise.name(), null, invitation.expiresAt());
    }

    @Override
    @Transactional
    public void accept(String rawToken, String username, String password) {
        ClientInvitationModel invitation = requirePendingInvitation(rawToken);
        User client = createClientAccount(invitation, username, password);
        invitationRepository.markAccepted(invitation.id(), clock.instant());
        if (isBorrowerInvitation(invitation)) {
            grantBorrowerAccess(invitation, client);
        } else {
            grantEnterpriseAccess(invitation, client);
        }
    }

    private User createClientAccount(ClientInvitationModel invitation, String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw AuthException.of(USERNAME_TAKEN);
        }
        User newUser = User.getBuilder()
            .username(username)
            .password(passwordEncoder.encode(password))
            .email(invitation.email())
            .role(UserRole.CLIENT)
            .preferredLanguage(requestContext.getLanguage())
            .build();
        try {
            return userRepository.save(newUser);
        } catch (DuplicateEntryException e) {
            throw AuthException.of(USERNAME_TAKEN, e);
        }
    }

    private void grantEnterpriseAccess(ClientInvitationModel invitation, User client) {
        String username = client.getUsername();
        EnterpriseUserLinkModel link = linkRepository.create(invitation.enterpriseId(), client.getId(),
            invitation.role(), EnterpriseLinkStatus.APPROVED, EnterpriseLinkOrigin.EMAIL_INVITATION, null,
            username);
        linkRepository.updateStatus(link.id(), EnterpriseLinkStatus.APPROVED, invitation.createdByUsername(),
            clock.instant());

        String enterpriseName = enterpriseRepository.findById(invitation.enterpriseId())
            .map(EnterpriseModel::name)
            .orElse("");
        afterCommit.send(() -> mailer.sendAccountConfirmation(invitation.email(), username, enterpriseName,
            client.getPreferredLanguage()));

        auditService.log(username, AuditAction.CLIENT_INVITATION_ACCEPTED,
            "invitationId=" + invitation.id() + ", enterpriseId=" + invitation.enterpriseId());
        auditService.log(invitation.createdByUsername(), AuditAction.ENTERPRISE_USER_LINK_APPROVED,
            "linkId=" + link.id() + ", enterpriseId=" + invitation.enterpriseId() + ", userId=" + client.getId()
                + ", origin=EMAIL_INVITATION");
    }

    private void grantBorrowerAccess(ClientInvitationModel invitation, User client) {
        String username = client.getUsername();
        borrowerRepository.link(invitation.demandId(), client.getId(), BorrowerLinkOrigin.EMAIL_INVITATION,
            invitation.createdByUsername());
        String reference = requireDemand(invitation.demandId()).reference();
        afterCommit.send(() -> mailer.sendBorrowerAccountConfirmation(invitation.email(), username, reference,
            client.getPreferredLanguage()));
        auditService.log(username, AuditAction.CLIENT_INVITATION_ACCEPTED,
            "invitationId=" + invitation.id() + ", demandId=" + invitation.demandId() + ", userId=" + client.getId()
                + ", origin=EMAIL_INVITATION");
    }

    private static boolean isBorrowerInvitation(ClientInvitationModel invitation) {
        return invitation.enterpriseId() == null;
    }

    private DemandModel requireDemand(Long demandId) {
        return workflowRepository.findDemandById(demandId).orElseThrow(() -> WorkflowException.of(DEMAND_NOT_FOUND));
    }

    private ClientInvitationModel requirePendingInvitation(String rawToken) {
        ClientInvitationModel invitation = invitationRepository.findByTokenHash(TokenHasher.hash(rawToken))
            .orElseThrow(() -> ClientInvitationException.of(TOKEN_NOT_FOUND));
        if (invitation.status() != ClientInvitationStatus.PENDING) {
            throw ClientInvitationException.of(TOKEN_NOT_FOUND);
        }
        if (invitation.expiresAt().isBefore(clock.instant())) {
            throw ClientInvitationException.of(TOKEN_EXPIRED);
        }
        return invitation;
    }
}
