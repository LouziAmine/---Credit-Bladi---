package com.credit.bladi.model.verification;

import com.credit.bladi.model.enums.BamScenario;
import com.credit.bladi.model.enums.CentraleRisquesStatus;
import com.credit.bladi.model.enums.ScipStatus;
import com.credit.bladi.model.enums.SimulatedRecommendation;
import java.time.Instant;
import java.util.List;

public record BamVerificationResult(
    String verifiedIdentifier,
    String identifierType,
    ScipStatus scipStatus,
    List<ChequeIncident> chequeIncidents,
    CentraleRisquesStatus centraleRisquesStatus,
    List<OutstandingLoan> outstandingLoans,
    SimulatedRecommendation recommendation,
    BamScenario scenario,
    Instant generatedAt
) {
}
