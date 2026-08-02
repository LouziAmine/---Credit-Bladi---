package com.credit.bladi.perf.scenarios.simulation.csp;

import static io.gatling.javaapi.core.CoreDsl.csv;

import com.credit.bladi.perf.scenarios.simulation.CspSimulationScenario;
import com.credit.bladi.perf.utils.SourceFile;
import io.gatling.javaapi.core.FeederBuilder;

public class ProfSimulationScenario extends CspSimulationScenario {

    @Override
    protected String getCspCode() {
        return "PROF";
    }

    @Override
    protected FeederBuilder<String> requestFeeder() {
        return csv(SourceFile.SIMULATION_PROF_SOURCE).random();
    }
}