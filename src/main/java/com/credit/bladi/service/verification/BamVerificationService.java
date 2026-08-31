package com.credit.bladi.service.verification;

import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.BamScenario;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.verification.BamVerificationResult;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import com.credit.bladi.service.verification.bam.IBamVerificationGateway;
import org.springframework.stereotype.Service;

@Service
public class BamVerificationService implements IBamVerificationService {
    private final IBamVerificationGateway bamVerificationGateway;

    private final IAuditService auditService;

    private final ICurrentActorProvider currentActorProvider;

    public BamVerificationService(IBamVerificationGateway bamVerificationGateway, IAuditService auditService,
            ICurrentActorProvider currentActorProvider) {
        this.bamVerificationGateway = bamVerificationGateway;
        this.auditService = auditService;
        this.currentActorProvider = currentActorProvider;
    }

    @Override
    public BamVerificationResult verify(EnterpriseType enterpriseType, String identifier, String companyName,
            BamScenario forcedScenario) {
        BamVerificationResult result = bamVerificationGateway.verify(enterpriseType, identifier, companyName,
            forcedScenario);

        auditService.log(currentActorProvider.getCurrentUsername(), AuditAction.BAM_VERIFICATION_MOCK_RUN,
            "enterpriseType=" + enterpriseType + ", companyName=" + companyName
                + ", scenario=" + result.scenario() + ", recommendation=" + result.recommendation());

        return result;
    }
}
