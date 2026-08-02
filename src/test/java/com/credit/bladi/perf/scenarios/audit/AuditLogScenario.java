package com.credit.bladi.perf.scenarios.audit;

import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;

import com.credit.bladi.perf.utils.AuditLogUtils;
import com.credit.bladi.perf.utils.AuthUtils;
import com.credit.bladi.perf.utils.ClientIpUtils;
import com.credit.bladi.perf.utils.SourceFile;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import java.time.Duration;

public class AuditLogScenario {

    private static final Duration RAMP_UP = Duration.ofSeconds(10);
    private static final Duration LOAD_TIME = Duration.ofMinutes(2);

    private final ScenarioBuilder auditLogScenario = scenario("Audit Log Read (manager)")
        .exec(ClientIpUtils.assignSyntheticClientIp())
        .exec(AuthUtils.loginAsAdmin())
        .feed(csv(SourceFile.AUDIT_LOG_PAGINATION_SOURCE).circular())
        .exec(AuditLogUtils.getAll());

    public PopulationBuilder auditLogTest(double usersPerSec) {
        return auditLogScenario.injectOpen(
            rampUsersPerSec(0).to(usersPerSec).during(RAMP_UP),
            constantUsersPerSec(usersPerSec).during(LOAD_TIME)
        );
    }
}
