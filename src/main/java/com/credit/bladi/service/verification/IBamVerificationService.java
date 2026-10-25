package com.credit.bladi.service.verification;

import com.credit.bladi.model.enums.BamScenario;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.verification.BamVerificationResult;

public interface IBamVerificationService {
    BamVerificationResult verify(EnterpriseType enterpriseType, String identifier, String companyName,
        BamScenario forcedScenario);
}
