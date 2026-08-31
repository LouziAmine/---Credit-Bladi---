package com.credit.bladi.perf.utils;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.doIf;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.ChainBuilder;

public class CreditRateUtils {
    public static ChainBuilder getAll() {
        return exec(
            http("GET /api/v1/credit-rates")
                .get("/api/v1/credit-rates")
                .queryParam("page", "#{page}")
                .queryParam("size", "#{size}")
                .check(status().is(200))
                .check(jsonPath("$.items[0].id").optional().saveAs("creditRateId"))
        );
    }

    // Ids are UUIDs: the detail and patch requests use the first listed record. A listed page past the
    // last one is empty, so the first page is read when no id is known yet.
    private static ChainBuilder rememberFirstId() {
        return doIf(session -> !session.contains("creditRateId")).then(exec(
            http("GET /api/v1/credit-rates (first record)")
                .get("/api/v1/credit-rates")
                .queryParam("page", "1")
                .queryParam("size", "1")
                .check(status().is(200))
                .check(jsonPath("$.items[0].id").saveAs("creditRateId"))
        ));
    }

    public static ChainBuilder getById() {
        return rememberFirstId().exec(
            http("GET /api/v1/credit-rates/{id}")
                .get("/api/v1/credit-rates/#{creditRateId}")
                .check(status().is(200))
        );
    }

    public static ChainBuilder patchRates() {
        return exec(
            http("PATCH /api/v1/credit-rates/{id}")
                .patch("/api/v1/credit-rates/#{creditRateId}")
                .body(StringBody("{\"rateMin\":#{rateMin},\"rateMax\":#{rateMax}}"))
                .check(status().is(204))
        );
    }
}
