package com.credit.bladi.perf.utils;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.ChainBuilder;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class MortgageCreditUtils {
    private static final String BODY_TEMPLATE =
        "{"
        + "\"applicantName\":\"#{applicantName}\","
        + "\"email\":\"#{email}\","
        + "\"phone\":\"#{phone}\","
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

    public static ChainBuilder assignUniqueApplicant() {
        return exec(session -> {
            String id = UUID.randomUUID().toString().substring(0, 12);
            return session
                .set("applicantName", "Gatling " + id)
                .set("email", "gatling-" + id + "@example.test")
                .set("phone", "06" + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000)));
        });
    }

    public static ChainBuilder simulate() {
        return exec(
            assignUniqueApplicant(),
            http("POST /api/v1/mortgage-credit [#{cspCode}]")
                .post("/api/v1/mortgage-credit")
                .body(StringBody(BODY_TEMPLATE))
                .check(status().is(200))
                .check(jsonPath("$.applicableRate").exists())
                .check(jsonPath("$.taeg").exists())
        );
    }
}
