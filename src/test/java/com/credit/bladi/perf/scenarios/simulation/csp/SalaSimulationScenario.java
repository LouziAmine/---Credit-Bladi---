package com.credit.bladi.perf.scenarios.simulation.csp;

import static io.gatling.javaapi.core.CoreDsl.csv;

import com.credit.bladi.perf.scenarios.simulation.CspSimulationScenario;
import com.credit.bladi.perf.utils.SourceFile;
import io.gatling.javaapi.core.FeederBuilder;

public class SalaSimulationScenario extends CspSimulationScenario {

    @Override
    protected String getCspCode() {
        return "SALA";
    }

    @Override
    protected FeederBuilder<String> requestFeeder() {
        return csv(SourceFile.SIMULATION_SALA_SOURCE).random();
    }
}