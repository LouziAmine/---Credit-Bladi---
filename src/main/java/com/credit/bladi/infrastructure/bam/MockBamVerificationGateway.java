package com.credit.bladi.infrastructure.bam;

import com.credit.bladi.model.enums.BamScenario;
import com.credit.bladi.model.enums.CentraleRisquesStatus;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.enums.RepaymentStatus;
import com.credit.bladi.model.enums.ScipStatus;
import com.credit.bladi.model.enums.SimulatedRecommendation;
import com.credit.bladi.model.verification.BamVerificationResult;
import com.credit.bladi.model.verification.ChequeIncident;
import com.credit.bladi.model.verification.OutstandingLoan;
import com.credit.bladi.service.verification.bam.IBamVerificationGateway;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "bam.verification", name = "provider", havingValue = "mock", matchIfMissing = true)
public class MockBamVerificationGateway implements IBamVerificationGateway {
    private static final Logger LOGGER = LogManager.getLogger(MockBamVerificationGateway.class);

    private record Weighted(BamScenario scenario, int weightPercent) {
    }

    private static final Map<EnterpriseType, List<Weighted>> DISTRIBUTION = new EnumMap<>(EnterpriseType.class);

    static {
        DISTRIBUTION.put(EnterpriseType.TPE, List.of(
            new Weighted(BamScenario.CLEAR, 70),
            new Weighted(BamScenario.MINOR_INCIDENT, 20),
            new Weighted(BamScenario.ACTIVE_INCIDENT, 10)));
        DISTRIBUTION.put(EnterpriseType.AUTO_ENTREPRENEUR, List.of(
            new Weighted(BamScenario.CLEAR, 60),
            new Weighted(BamScenario.PERSONAL_EXPOSURE_DETECTED, 25),
            new Weighted(BamScenario.ACTIVE_INCIDENT, 15)));
        DISTRIBUTION.put(EnterpriseType.PME, List.of(
            new Weighted(BamScenario.CLEAR, 75),
            new Weighted(BamScenario.MULTIPLE_EXPOSURES, 15),
            new Weighted(BamScenario.ACTIVE_INCIDENT, 10)));
        DISTRIBUTION.put(EnterpriseType.ETI, List.of(
            new Weighted(BamScenario.CLEAR, 80),
            new Weighted(BamScenario.MULTIPLE_EXPOSURES, 15),
            new Weighted(BamScenario.ACTIVE_INCIDENT, 5)));
        DISTRIBUTION.put(EnterpriseType.GE, List.of(
            new Weighted(BamScenario.CLEAR, 85),
            new Weighted(BamScenario.CONSOLIDATED_GROUP_EXPOSURE, 10),
            new Weighted(BamScenario.REQUIRES_MANUAL_REVIEW, 5)));
    }

    private static final List<String> FICTIONAL_BANKS = List.of(
        "Atlas Al Maghrib Bank (fictional)",
        "Al Amane Credit Bank (fictional)",
        "Strait of Gibraltar Bank (fictional)",
        "Maghreb Finance Credit (fictional)",
        "Bank A",
        "Bank B"
    );

    private final int minLatencyMs;

    private final int maxLatencyMs;

    private final Clock clock;

    public MockBamVerificationGateway(
            @Value("${bam.verification.mock.latency.min-ms:500}") int minLatencyMs,
            @Value("${bam.verification.mock.latency.max-ms:1500}") int maxLatencyMs, Clock clock) {
        this.clock = clock;
        this.minLatencyMs = minLatencyMs;
        this.maxLatencyMs = maxLatencyMs;
    }

    @Override
    public BamVerificationResult verify(EnterpriseType enterpriseType, String identifier, String companyName,
            BamScenario forcedScenario) {
        simulateNetworkLatency();

        BamScenario scenario = forcedScenario != null ? forcedScenario : drawScenario(enterpriseType);
        LOGGER.info("BAM_VERIFICATION_MOCK enterpriseType={} companyName={} scenario={} forced={}",
            enterpriseType, companyName, scenario, forcedScenario != null);

        return switch (scenario) {
            case CLEAR -> clear(enterpriseType, identifier, scenario);
            case MINOR_INCIDENT -> withChequeIncident(enterpriseType, identifier, scenario, true,
                SimulatedRecommendation.FAVORABLE);
            case ACTIVE_INCIDENT -> withChequeIncident(enterpriseType, identifier, scenario, false,
                SimulatedRecommendation.UNFAVORABLE);
            case PERSONAL_EXPOSURE_DETECTED -> withOutstandingLoans(enterpriseType, identifier, scenario,
                List.of(randomOutstandingLoan("Personal credit")), SimulatedRecommendation.TO_REVIEW);
            case MULTIPLE_EXPOSURES -> withOutstandingLoans(enterpriseType, identifier, scenario,
                List.of(randomOutstandingLoan("Investment credit"), randomOutstandingLoan("Cash-flow credit")),
                SimulatedRecommendation.TO_REVIEW);
            case CONSOLIDATED_GROUP_EXPOSURE -> withOutstandingLoans(enterpriseType, identifier, scenario,
                List.of(randomOutstandingLoan("Investment credit (subsidiary 1)"),
                    randomOutstandingLoan("Cash-flow line (subsidiary 2)"),
                    randomOutstandingLoan("Leasing (parent company)")),
                SimulatedRecommendation.TO_REVIEW);
            case REQUIRES_MANUAL_REVIEW -> new BamVerificationResult(identifier,
                identifierType(enterpriseType), ScipStatus.CLEAR, List.of(), CentraleRisquesStatus.CLEAR, List.of(),
                SimulatedRecommendation.TO_REVIEW, scenario, clock.instant());
        };
    }

    private void simulateNetworkLatency() {
        int latencyMs = minLatencyMs >= maxLatencyMs
            ? minLatencyMs
            : ThreadLocalRandom.current().nextInt(minLatencyMs, maxLatencyMs + 1);
        if (latencyMs <= 0) {
            return;
        }
        try {
            Thread.sleep(latencyMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private BamScenario drawScenario(EnterpriseType enterpriseType) {
        List<Weighted> weights = DISTRIBUTION.get(enterpriseType);
        int roll = ThreadLocalRandom.current().nextInt(100);
        int cumulative = 0;
        for (Weighted weighted : weights) {
            cumulative += weighted.weightPercent();
            if (roll < cumulative) {
                return weighted.scenario();
            }
        }
        return weights.get(weights.size() - 1).scenario();
    }

    private BamVerificationResult clear(EnterpriseType enterpriseType, String identifier, BamScenario scenario) {
        return new BamVerificationResult(identifier, identifierType(enterpriseType), ScipStatus.CLEAR, List.of(),
            CentraleRisquesStatus.CLEAR, List.of(), SimulatedRecommendation.FAVORABLE, scenario, clock.instant());
    }

    private BamVerificationResult withChequeIncident(EnterpriseType enterpriseType, String identifier,
            BamScenario scenario, boolean resolved, SimulatedRecommendation recommendation) {
        ChequeIncident incident = new ChequeIncident("unpaid_cheque", randomRecentDate(), randomAmount(2000, 50000),
            resolved);
        return new BamVerificationResult(identifier, identifierType(enterpriseType), ScipStatus.INCIDENT_DETECTED,
            List.of(incident), CentraleRisquesStatus.CLEAR, List.of(), recommendation, scenario, clock.instant());
    }

    private BamVerificationResult withOutstandingLoans(EnterpriseType enterpriseType, String identifier,
            BamScenario scenario, List<OutstandingLoan> outstandingLoans, SimulatedRecommendation recommendation) {
        return new BamVerificationResult(identifier, identifierType(enterpriseType), ScipStatus.CLEAR, List.of(),
            CentraleRisquesStatus.EXPOSURE_DETECTED, outstandingLoans, recommendation, scenario, clock.instant());
    }

    private OutstandingLoan randomOutstandingLoan(String creditType) {
        String institution = FICTIONAL_BANKS.get(ThreadLocalRandom.current().nextInt(FICTIONAL_BANKS.size()));
        return new OutstandingLoan(institution, creditType, randomAmount(100000, 5000000), RepaymentStatus.CURRENT);
    }

    private static String identifierType(EnterpriseType enterpriseType) {
        return enterpriseType == EnterpriseType.AUTO_ENTREPRENEUR ? "ICE+CIN" : "ICE";
    }

    private static BigDecimal randomAmount(int minInclusive, int maxInclusive) {
        int amount = ThreadLocalRandom.current().nextInt(minInclusive, maxInclusive + 1);
        return BigDecimal.valueOf(amount);
    }

    private LocalDate randomRecentDate() {
        int daysAgo = ThreadLocalRandom.current().nextInt(30, 548);
        return LocalDate.now(clock).minusDays(daysAgo);
    }
}
