package com.credit.bladi.perf.scenarios.simulation.csp;

import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.scenario;

import com.credit.bladi.perf.scenarios.simulation.CspSimulationScenario;
import com.credit.bladi.perf.utils.ClientIpUtils;
import com.credit.bladi.perf.utils.SimulationUtils;
import com.credit.bladi.perf.utils.SourceFile;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;

public class MreSimulationScenario extends CspSimulationScenario {

    @Override
    protected String getCspCode() {
        return "MRE";
    }

    @Override
    protected FeederBuilder<String> requestFeeder() {
        return csv(SourceFile.SIMULATION_MRE_SOURCE).random();
    }

    @Override
    protected ScenarioBuilder simulateScenario() {
        return scenario("Mortgage Simulation [MRE]")
            .exec(ClientIpUtils.assignSyntheticClientIp())
            .feed(requestFeeder())
            .exec(SimulationUtils.simulate());
    }
}