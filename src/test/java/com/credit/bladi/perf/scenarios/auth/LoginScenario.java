package com.credit.bladi.perf.scenarios.auth;

import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;

import com.credit.bladi.perf.utils.AuthUtils;
import com.credit.bladi.perf.utils.ClientIpUtils;
import com.credit.bladi.perf.utils.SourceFile;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import java.time.Duration;

public class LoginScenario {
    private static final Duration RAMP_UP = Duration.ofSeconds(20);
    private static final Duration LOAD_TIME = Duration.ofMinutes(1);

    private final ScenarioBuilder loginScenario = scenario("Auth Login")
        .exec(ClientIpUtils.assignSyntheticClientIp())
        .feed(csv(SourceFile.AUTH_LOGIN_SOURCE).circular())
        .exec(AuthUtils.login());

    public PopulationBuilder loginTest(double usersPerSec) {
        return loginScenario.injectOpen(
            rampUsersPerSec(0).to(usersPerSec).during(RAMP_UP),
            constantUsersPerSec(usersPerSec).during(LOAD_TIME)
        );
    }
}
