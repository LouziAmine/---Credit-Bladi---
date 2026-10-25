package com.credit.bladi.perf.scenarios.auth;

import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;

import com.credit.bladi.perf.utils.AuthUtils;
import com.credit.bladi.perf.utils.ClientIpUtils;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import java.time.Duration;

public class RegisterScenario {
    private static final Duration RAMP_UP = Duration.ofSeconds(20);
    private static final Duration LOAD_TIME = Duration.ofMinutes(1);

    private final ScenarioBuilder registerScenario = scenario("Auth Register")
        .exec(ClientIpUtils.assignSyntheticClientIp())
        .exec(AuthUtils.register());

    public PopulationBuilder registerTest(double usersPerSec) {
        return registerScenario.injectOpen(
            rampUsersPerSec(0).to(usersPerSec).during(RAMP_UP),
            constantUsersPerSec(usersPerSec).during(LOAD_TIME)
        );
    }
}
