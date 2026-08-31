package com.credit.bladi.service.enterprise;

import static com.credit.bladi.common.PiiMasker.maskEmail;
import static com.credit.bladi.exception.AuthException.AuthExceptionType.USER_NOT_FOUND;
import static com.credit.bladi.exception.EnterpriseException.EnterpriseExceptionType.ENTERPRISE_NOT_FOUND;
import static com.credit.bladi.exception.EnterpriseUserLinkException.EnterpriseUserLinkExceptionType.ENTERPRISE_NOT_LINKED;
import static com.credit.bladi.exception.EnterpriseUserLinkException.EnterpriseUserLinkExceptionType.LINK_ALREADY_ACTIVE;
import static com.credit.bladi.exception.EnterpriseUserLinkException.EnterpriseUserLinkExceptionType.LINK_NOT_APPROVED;
import static com.credit.bladi.exception.EnterpriseUserLinkException.EnterpriseUserLinkExceptionType.LINK_NOT_FOUND;
import static com.credit.bladi.exception.EnterpriseUserLinkException.EnterpriseUserLinkExceptionType.LINK_NOT_PENDING;
import static com.credit.bladi.exception.EnterpriseUserLinkException.EnterpriseUserLinkExceptionType.REGISTRATION_NUMBER_NOT_FOUND;
import static com.credit.bladi.exception.EnterpriseUserLinkException.EnterpriseUserLinkExceptionType.USER_NOT_CLIENT;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.exception.AuthException;
import com.credit.bladi.exception.EnterpriseException;
import com.credit.bladi.exception.EnterpriseUserLinkException;
import com.credit.bladi.model.client.EnterpriseModel;
import com.credit.bladi.model.enterprise.EnterpriseUserLinkModel;
import com.credit.bladi.model.enterprise.event.EnterpriseLinkStatusChangedEvent;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.EnterpriseLinkOrigin;
import com.credit.bladi.model.enums.EnterpriseLinkRole;
import com.credit.bladi.model.enums.EnterpriseLinkStatus;
import com.credit.bladi.model.enums.Language;
import com.credit.bladi.model.enums.NotificationType;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.notification.LocalizedText;
import com.credit.bladi.model.notification.NotificationMessage;
import com.credit.bladi.model.user.User;
import com.credit.bladi.repository.DuplicateEntryException;
import com.credit.bladi.repository.client.IEnterpriseRepository;
import com.credit.bladi.repository.enterprise.IEnterpriseUserLinkRepository;
import com.credit.bladi.repository.user.IUserRepository;
import com.credit.bladi.service.invitation.IClientInvitationService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import java.time.Clock;
import java.util.List;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnterpriseUserLinkService implements IEnterpriseUserLinkService, IApprovedEnterpriseLinksProvider,
        IEnterpriseLinkClassificationResolver {
    private static final Logger LOGGER = LogManager.getLogger(EnterpriseUserLinkService.class);

    private final IEnterpriseUserLinkRepository linkRepository;

    private final IEnterpriseRepository enterpriseRepository;

    private final IUserRepository userRepository;

    private final ApplicationEventPublisher eventPublisher;

    private final ICurrentActorProvider currentActorProvider;

    private final IClientInvitationService invitationService;

    private final Clock clock;

    public EnterpriseUserLinkService(IEnterpriseUserLinkRepository linkRepository,
            IEnterpriseRepository enterpriseRepository, IUserRepository userRepository,
            ApplicationEventPublisher eventPublisher, ICurrentActorProvider currentActorProvider,
            IClientInvitationService invitationService, Clock clock) {
        this.linkRepository = linkRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.currentActorProvider = currentActorProvider;
        this.invitationService = invitationService;
        this.clock = clock;
    }

    @Override
    @Transactional
    public EnterpriseUserLinkModel inviteClient(Long enterpriseId, String username, EnterpriseLinkRole role) {
        String actor = currentActorProvider.getCurrentUsername();
        EnterpriseModel enterprise = enterpriseRepository.findById(enterpriseId)
            .orElseThrow(() -> EnterpriseException.of(ENTERPRISE_NOT_FOUND));
        User target = userRepository.findByUsername(username).orElseThrow(() -> AuthException.of(USER_NOT_FOUND));
        if (target.getRole() != UserRole.CLIENT) {
            throw EnterpriseUserLinkException.of(USER_NOT_CLIENT);
        }
        assertNoActiveLink(enterpriseId, target.getId());

        EnterpriseUserLinkModel link;
        try {
            link = linkRepository.create(enterpriseId, target.getId(), role, EnterpriseLinkStatus.APPROVED,
                EnterpriseLinkOrigin.MANAGER_INVITE, null, actor);
        } catch (DuplicateEntryException e) {
            throw EnterpriseUserLinkException.of(LINK_ALREADY_ACTIVE);
        }
        link = linkRepository.updateStatus(link.id(), EnterpriseLinkStatus.APPROVED, actor, clock.instant());

        eventPublisher.publishEvent(new EnterpriseLinkStatusChangedEvent(actor,
            AuditAction.ENTERPRISE_USER_LINK_INVITED,
            "linkId=" + link.id() + ", enterpriseId=" + enterpriseId + ", username=" + username
                + ", role=" + role,
            List.of(message(target, NotificationType.ENTERPRISE_LINK_INVITED, LocalizedText.of("linkGranted.title"),
                LocalizedText.of("linkGranted.body", enterprise.name())))));
        return link;
    }

    @Override
    @Transactional(readOnly = true)
    public Long resolveLinkId(UUID publicId) {
        return linkRepository.findIdByPublicId(publicId)
            .orElseThrow(() -> EnterpriseUserLinkException.of(LINK_NOT_FOUND));
    }

    @Override
    @Transactional
    public EnterpriseUserLinkModel claimEnterprise(String registrationNumber, EnterpriseLinkRole role) {
        String actor = currentActorProvider.getCurrentUsername();
        EnterpriseModel enterprise = enterpriseRepository.findByRegistrationNumber(registrationNumber)
            .orElseThrow(() -> EnterpriseUserLinkException.of(REGISTRATION_NUMBER_NOT_FOUND));
        User requester = userRepository.findByUsername(actor).orElseThrow(() -> AuthException.of(USER_NOT_FOUND));
        assertNoActiveLink(enterprise.id(), requester.getId());

        EnterpriseUserLinkModel link;
        try {
            link = linkRepository.create(enterprise.id(), requester.getId(), role, EnterpriseLinkStatus.PENDING,
                EnterpriseLinkOrigin.CLIENT_CLAIM, registrationNumber, actor);
        } catch (DuplicateEntryException e) {
            throw EnterpriseUserLinkException.of(LINK_ALREADY_ACTIVE);
        }

        LocalizedText body = LocalizedText.of("linkClaimPending.body", actor, enterprise.name());
        List<NotificationMessage> managerNotifications = userRepository.findAllByRole(UserRole.MANAGER).stream()
            .map(manager -> new NotificationMessage(manager.getUsername(), manager.getEmail(), null,
                manager.getPreferredLanguage(), NotificationType.ENTERPRISE_LINK_CLAIM_PENDING,
                LocalizedText.of("linkClaimPending.title"), body,
                "/admin/enterprise-links"))
            .toList();
        eventPublisher.publishEvent(new EnterpriseLinkStatusChangedEvent(actor,
            AuditAction.ENTERPRISE_USER_LINK_CLAIMED,
            "linkId=" + link.id() + ", enterpriseId=" + enterprise.id() + ", registrationNumber="
                + registrationNumber,
            managerNotifications));
        return link;
    }

    @Override
    @Transactional
    public EnterpriseUserLinkModel approve(Long linkId) {
        String actor = currentActorProvider.getCurrentUsername();
        EnterpriseUserLinkModel link = requirePendingLink(linkId);
        EnterpriseUserLinkModel updated = linkRepository.decideIfPending(linkId, EnterpriseLinkStatus.APPROVED, actor,
            clock.instant());
        eventPublisher.publishEvent(new EnterpriseLinkStatusChangedEvent(actor,
            AuditAction.ENTERPRISE_USER_LINK_APPROVED,
            "linkId=" + linkId + ", enterpriseId=" + link.enterpriseId() + ", userId=" + link.userId(),
            linkTargetNotification(link, NotificationType.ENTERPRISE_LINK_APPROVED,
                LocalizedText.of("linkApproved.title"), LocalizedText.of("linkApproved.body"))));
        return updated;
    }

    @Override
    @Transactional
    public EnterpriseUserLinkModel reject(Long linkId, String reason) {
        String actor = currentActorProvider.getCurrentUsername();
        EnterpriseUserLinkModel link = requirePendingLink(linkId);
        EnterpriseUserLinkModel updated = linkRepository.decideIfPending(linkId, EnterpriseLinkStatus.REJECTED, actor,
            clock.instant());
        eventPublisher.publishEvent(new EnterpriseLinkStatusChangedEvent(actor,
            AuditAction.ENTERPRISE_USER_LINK_REJECTED,
            "linkId=" + linkId + ", enterpriseId=" + link.enterpriseId() + ", userId=" + link.userId()
                + ", reason=" + reason,
            linkTargetNotification(link, NotificationType.ENTERPRISE_LINK_REJECTED,
                LocalizedText.of("linkRejected.title"),
                LocalizedText.of("linkRejected.body", reason != null ? " : " + reason : ""))));
        return updated;
    }

    @Override
    @Transactional
    public EnterpriseUserLinkModel revoke(Long linkId) {
        String actor = currentActorProvider.getCurrentUsername();
        EnterpriseUserLinkModel link = linkRepository.findById(linkId)
            .orElseThrow(() -> EnterpriseUserLinkException.of(LINK_NOT_FOUND));
        if (link.status() != EnterpriseLinkStatus.APPROVED) {
            throw EnterpriseUserLinkException.of(LINK_NOT_APPROVED);
        }
        EnterpriseUserLinkModel updated = linkRepository.updateStatus(linkId, EnterpriseLinkStatus.REVOKED, actor,
            clock.instant());
        eventPublisher.publishEvent(new EnterpriseLinkStatusChangedEvent(actor,
            AuditAction.ENTERPRISE_USER_LINK_REVOKED,
            "linkId=" + linkId + ", enterpriseId=" + link.enterpriseId() + ", userId=" + link.userId(),
            linkTargetNotification(link, NotificationType.ENTERPRISE_LINK_REVOKED,
                LocalizedText.of("linkRevoked.title"), LocalizedText.of("linkRevoked.body"))));
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public CreditPage<EnterpriseUserLinkModel> listByEnterprise(Long enterpriseId, int page, int size) {
        return linkRepository.findByEnterpriseId(enterpriseId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public CreditPage<EnterpriseUserLinkModel> listPending(int page, int size) {
        return linkRepository.findByStatus(EnterpriseLinkStatus.PENDING, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnterpriseUserLinkModel> listMyLinks() {
        User requester = userRepository.findByUsername(currentActorProvider.getCurrentUsername())
            .orElseThrow(() -> AuthException.of(USER_NOT_FOUND));
        return linkRepository.findByUserId(requester.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnterpriseUserLinkModel> listMyApprovedLinks() {
        User requester = userRepository.findByUsername(currentActorProvider.getCurrentUsername())
            .orElseThrow(() -> AuthException.of(USER_NOT_FOUND));
        return linkRepository.findApprovedByUserId(requester.getId());
    }

    private List<Long> getApprovedEnterpriseIdsForCurrentUser() {
        return listMyApprovedLinks().stream()
            .map(EnterpriseUserLinkModel::enterpriseId)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public void assertCurrentUserApprovedForEnterprise(Long enterpriseId) {
        if (!getApprovedEnterpriseIdsForCurrentUser().contains(enterpriseId)) {
            throw EnterpriseUserLinkException.of(ENTERPRISE_NOT_LINKED);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnterpriseUserLinkModel> listApprovedForEnterprise(Long enterpriseId) {
        return linkRepository.findApprovedByEnterpriseId(enterpriseId);
    }

    @Override
    @Transactional
    public void resolveClientForClassification(Long enterpriseId, String contactEmail, Language contactLanguage,
            Long demandId, String actorUsername) {
        if (contactEmail == null || contactEmail.isBlank()) {
            return;
        }
        List<User> matches = userRepository.findClientsByEmail(contactEmail);
        if (matches.size() == 1) {
            User client = matches.get(0);
            if (linkRepository.findActiveLink(enterpriseId, client.getId()).isPresent()) {
                return;
            }
            EnterpriseModel enterprise = enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> EnterpriseException.of(ENTERPRISE_NOT_FOUND));
            EnterpriseUserLinkModel link = linkRepository.create(enterpriseId, client.getId(),
                EnterpriseLinkRole.RESPONSABLE, EnterpriseLinkStatus.APPROVED,
                EnterpriseLinkOrigin.AUTO_MATCHED_ON_CLASSIFICATION, null, actorUsername);
            link = linkRepository.updateStatus(link.id(), EnterpriseLinkStatus.APPROVED, actorUsername,
                clock.instant());
            eventPublisher.publishEvent(new EnterpriseLinkStatusChangedEvent(actorUsername,
                AuditAction.ENTERPRISE_USER_LINK_APPROVED,
                "linkId=" + link.id() + ", enterpriseId=" + enterpriseId + ", userId=" + client.getId()
                    + ", origin=AUTO_MATCHED_ON_CLASSIFICATION, demandId=" + demandId,
                List.of(message(client, NotificationType.ENTERPRISE_LINK_APPROVED,
                    LocalizedText.of("linkGranted.title"), LocalizedText.of("linkGranted.body", enterprise.name())))));
        } else if (matches.isEmpty()) {
            invitationService.createInvitation(enterpriseId, contactEmail, contactLanguage,
                EnterpriseLinkRole.RESPONSABLE, demandId, actorUsername);
        } else {
            LOGGER.info("CLASSIFICATION_CLIENT_RESOLUTION_AMBIGUOUS enterpriseId={}, contactEmail={}, matches={}",
                enterpriseId, maskEmail(contactEmail), matches.size());
        }
    }

    private void assertNoActiveLink(Long enterpriseId, Long userId) {
        if (linkRepository.findActiveLink(enterpriseId, userId).isPresent()) {
            throw EnterpriseUserLinkException.of(LINK_ALREADY_ACTIVE);
        }
    }

    private EnterpriseUserLinkModel requirePendingLink(Long linkId) {
        EnterpriseUserLinkModel link = linkRepository.findById(linkId)
            .orElseThrow(() -> EnterpriseUserLinkException.of(LINK_NOT_FOUND));
        if (link.status() != EnterpriseLinkStatus.PENDING) {
            throw EnterpriseUserLinkException.of(LINK_NOT_PENDING);
        }
        return link;
    }

    private List<NotificationMessage> linkTargetNotification(EnterpriseUserLinkModel link, NotificationType type,
            LocalizedText title, LocalizedText body) {
        return userRepository.findById(link.userId())
            .map(target -> List.of(message(target, type, title, body)))
            .orElse(List.of());
    }

    private static NotificationMessage message(User target, NotificationType type, LocalizedText title,
            LocalizedText body) {
        return new NotificationMessage(target.getUsername(), target.getEmail(), null,
            target.getPreferredLanguage(), type, title, body, "/portfolio");
    }
}
