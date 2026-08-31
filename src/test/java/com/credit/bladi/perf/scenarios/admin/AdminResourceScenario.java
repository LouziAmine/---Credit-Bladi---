package com.credit.bladi.perf.scenarios.admin;

import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;

import com.credit.bladi.perf.utils.AuthUtils;
import com.credit.bladi.perf.utils.ClientIpUtils;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import java.time.Duration;

public class AdminResourceScenario {
    private static final Duration RAMP_UP = Duration.ofSeconds(10);
    private static final Duration LOAD_TIME = Duration.ofMinutes(2);

    private final ScenarioBuilder scenarioBuilder;

    public AdminResourceScenario(
            String scenarioName,
            String paginationSource, ChainBuilder listStep, ChainBuilder detailStep,
            String patchSource, ChainBuilder patchStep) {
        this.scenarioBuilder = scenario(scenarioName)
            .exec(ClientIpUtils.assignSyntheticClientIp())
            .exec(AuthUtils.loginAsAdmin())
            .feed(csv(paginationSource).circular())
            .exec(listStep)
            .exec(detailStep)
            .feed(csv(patchSource).circular())
            .exec(patchStep);
    }

    public PopulationBuilder test(double usersPerSec) {
        return scenarioBuilder.injectOpen(
            rampUsersPerSec(0).to(usersPerSec).during(RAMP_UP),
            constantUsersPerSec(usersPerSec).during(LOAD_TIME)
        );
    }
}
