package com.credit.bladi.perf.scenarios.mortgage.csp;

import static io.gatling.javaapi.core.CoreDsl.csv;

import com.credit.bladi.perf.scenarios.mortgage.CspMortgageCreditScenario;
import com.credit.bladi.perf.utils.SourceFile;
import io.gatling.javaapi.core.FeederBuilder;

public class SalaMortgageCreditScenario extends CspMortgageCreditScenario {
    @Override
    protected String getCspCode() {
        return "SALA";
    }

    @Override
    protected FeederBuilder<String> requestFeeder() {
        return csv(SourceFile.MORTGAGE_CREDIT_SALA_SOURCE).random();
    }
}
