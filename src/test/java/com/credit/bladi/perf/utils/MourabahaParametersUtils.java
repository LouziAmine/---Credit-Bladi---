package com.credit.bladi.perf.utils;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.ChainBuilder;

public class MourabahaParametersUtils {

    private static final long SEEDED_ID = 1L;

    public static ChainBuilder getAll() {
        return exec(
            http("GET /api/v1/mourabaha-parameters")
                .get("/api/v1/mourabaha-parameters")
                .queryParam("page", "#{page}")
                .queryParam("size", "#{size}")
                .check(status().is(200))
        );
    }

    public static ChainBuilder getById() {
        return exec(
            http("GET /api/v1/mourabaha-parameters/{id}")
                .get("/api/v1/mourabaha-parameters/" + SEEDED_ID)
                .check(status().is(200))
        );
    }

    public static ChainBuilder patchRates() {
        return exec(
            http("PATCH /api/v1/mourabaha-parameters/{id}")
                .patch("/api/v1/mourabaha-parameters/" + SEEDED_ID)
                .body(StringBody("{\"profitRate\":#{profitRate},\"vatRate\":#{vatRate}}"))
                .check(status().is(204))
        );
    }
}
