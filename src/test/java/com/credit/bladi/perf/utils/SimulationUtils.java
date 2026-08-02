package com.credit.bladi.perf.utils;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.ChainBuilder;

public class SimulationUtils {

    private static final String BODY_TEMPLATE =
        "{"
        + "\"amount\":#{amount},"
        + "\"duration\":#{duration},"
        + "\"cspCode\":\"#{cspCode}\","
        + "\"productType\":\"#{productType}\","
        + "\"nationalityCode\":\"#{nationalityCode}\","
        + "\"moroccanResident\":#{moroccanResident},"
        + "\"propertyValue\":#{propertyValue},"
        + "\"monthlyIncome\":#{monthlyIncome},"
        + "\"age\":#{age}"
        + "}";

    public static ChainBuilder simulate() {
        return exec(
            http("POST /api/v1/simulation [#{cspCode}]")
                .post("/api/v1/simulation")
                .body(StringBody(BODY_TEMPLATE))
                .check(status().is(200))
                .check(jsonPath("$.applicableRate").exists())
                .check(jsonPath("$.taeg").exists())
        );
    }
}