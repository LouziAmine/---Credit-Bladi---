package com.credit.bladi.service.borrower;

import static com.credit.bladi.common.PiiMasker.maskEmail;
import static com.credit.bladi.exception.AuthException.AuthExceptionType.USER_NOT_FOUND;
import static com.credit.bladi.exception.EnterpriseUserLinkException.EnterpriseUserLinkExceptionType.DEMAND_NOT_LINKED;

import com.credit.bladi.exception.AuthException;
import com.credit.bladi.exception.EnterpriseUserLinkException;
import com.credit.bladi.model.audit.AuditTarget;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.BorrowerLinkOrigin;
import com.credit.bladi.model.user.User;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.repository.borrower.IMortgageBorrowerRepository;
import com.credit.bladi.repository.user.IUserRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.invitation.IClientInvitationService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MortgageBorrowerService implements IMortgageBorrowerService {
    private static final Logger LOGGER = LogManager.getLogger(MortgageBorrowerService.class);

    private final IMortgageBorrowerRepository borrowerRepository;

    private final IUserRepository userRepository;

    private final IClientInvitationService invitationService;

    private final IAuditService auditService;

    private final ICurrentActorProvider currentActorProvider;

    public MortgageBorrowerService(IMortgageBorrowerRepository borrowerRepository, IUserRepository userRepository,
            IClientInvitationService invitationService, IAuditService auditService,
            ICurrentActorProvider currentActorProvider) {
        this.borrowerRepository = borrowerRepository;
        this.userRepository = userRepository;
        this.invitationService = invitationService;
        this.auditService = auditService;
        this.currentActorProvider = currentActorProvider;
    }

    @Override
    @Transactional
    public void resolveBorrowerForClassification(DemandModel demand, String actorUsername) {
        String contactEmail = demand.contactEmail();
        if (contactEmail == null || contactEmail.isBlank()
                || borrowerRepository.findByDemandId(demand.id()).isPresent()) {
            return;
        }
        List<User> matches = userRepository.findClientsByEmail(contactEmail);
        if (matches.size() == 1) {
            User client = matches.get(0);
            borrowerRepository.link(demand.id(), client.getId(), BorrowerLinkOrigin.AUTO_MATCHED_ON_CLASSIFICATION,
                actorUsername);
            auditService.log(actorUsername, AuditAction.MORTGAGE_BORROWER_LINKED, AuditTarget.demand(demand.publicId()),
                "demandId=" + demand.id() + ", userId=" + client.getId() + ", origin=AUTO_MATCHED_ON_CLASSIFICATION");
        } else if (matches.isEmpty()) {
            invitationService.createBorrowerInvitation(demand.id(), contactEmail, demand.contactLanguage(),
                actorUsername);
        } else {
            LOGGER.info("CLASSIFICATION_BORROWER_RESOLUTION_AMBIGUOUS demandId={}, contactEmail={}, matches={}",
                demand.id(), maskEmail(contactEmail), matches.size());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void assertCurrentUserIsBorrower(Long demandId) {
        if (!borrowerRepository.isBorrower(demandId, currentUser().getId())) {
            throw EnterpriseUserLinkException.of(DEMAND_NOT_LINKED);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getMyDemandIds() {
        return borrowerRepository.findDemandIdsByUserId(currentUser().getId());
    }

    private User currentUser() {
        return userRepository.findByUsername(currentActorProvider.getCurrentUsername())
            .orElseThrow(() -> AuthException.of(USER_NOT_FOUND));
    }
}
