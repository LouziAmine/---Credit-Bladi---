package com.credit.bladi.perf.utils;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.ChainBuilder;

public class CreditRateUtils {

    private static final long SEEDED_ID = 1L;

    public static ChainBuilder getAll() {
        return exec(
            http("GET /api/v1/credit-rates")
                .get("/api/v1/credit-rates")
                .queryParam("page", "#{page}")
                .queryParam("size", "#{size}")
                .check(status().is(200))
        );
    }

    public static ChainBuilder getById() {
        return exec(
            http("GET /api/v1/credit-rates/{id}")
                .get("/api/v1/credit-rates/" + SEEDED_ID)
                .check(status().is(200))
        );
    }

    public static ChainBuilder patchRates() {
        return exec(
            http("PATCH /api/v1/credit-rates/{id}")
                .patch("/api/v1/credit-rates/" + SEEDED_ID)
                .body(StringBody("{\"rateMin\":#{rateMin},\"rateMax\":#{rateMax}}"))
                .check(status().is(204))
        );
    }
}
