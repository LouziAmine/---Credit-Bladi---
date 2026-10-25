package com.credit.bladi.service.verification.bam;

import com.credit.bladi.model.enums.BamScenario;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.verification.BamVerificationResult;

public interface IBamVerificationGateway {
    BamVerificationResult verify(EnterpriseType enterpriseType, String identifier, String companyName,
        BamScenario forcedScenario);
}
