package com.credit.bladi.perf.scenarios.simulation;

import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;

import com.credit.bladi.perf.utils.ClientIpUtils;
import com.credit.bladi.perf.utils.SimulationUtils;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import java.time.Duration;

public abstract class CspSimulationScenario {

    private static final Duration RAMP_UP = Duration.ofSeconds(30);
    private static final Duration LOAD_TIME = Duration.ofMinutes(2);

    protected abstract String getCspCode();

    protected abstract FeederBuilder<String> requestFeeder();

    protected ScenarioBuilder simulateScenario() {
        return scenario("Mortgage Simulation [" + getCspCode() + "]")
            .exec(ClientIpUtils.assignSyntheticClientIp())
            .feed(requestFeeder())
            .exec(session -> session.set("cspCode", getCspCode()))
            .exec(SimulationUtils.simulate());
    }

    public PopulationBuilder simulateTest(double usersPerSec) {
        return simulateScenario().injectOpen(
            rampUsersPerSec(0).to(usersPerSec).during(RAMP_UP),
            constantUsersPerSec(usersPerSec).during(LOAD_TIME)
        );
    }
}
