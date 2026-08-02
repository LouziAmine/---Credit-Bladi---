package com.credit.bladi.perf.scenarios.simulation.csp;

import static io.gatling.javaapi.core.CoreDsl.csv;

import com.credit.bladi.perf.scenarios.simulation.CspSimulationScenario;
import com.credit.bladi.perf.utils.SourceFile;
import io.gatling.javaapi.core.FeederBuilder;

public class IndeSimulationScenario extends CspSimulationScenario {

    @Override
    protected String getCspCode() {
        return "INDE";
    }

    @Override
    protected FeederBuilder<String> requestFeeder() {
        return csv(SourceFile.SIMULATION_INDE_SOURCE).random();
    }
}