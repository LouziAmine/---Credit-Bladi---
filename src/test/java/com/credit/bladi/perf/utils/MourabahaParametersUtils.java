package com.credit.bladi.perf.utils;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.doIf;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.ChainBuilder;

public class MourabahaParametersUtils {
    public static ChainBuilder getAll() {
        return exec(
            http("GET /api/v1/mourabaha-parameters")
                .get("/api/v1/mourabaha-parameters")
                .queryParam("page", "#{page}")
                .queryParam("size", "#{size}")
                .check(status().is(200))
                .check(jsonPath("$.items[0].id").optional().saveAs("mourabahaParametersId"))
        );
    }

    // Ids are UUIDs: the detail and patch requests use the first listed record. A listed page past the
    // last one is empty, so the first page is read when no id is known yet.
    private static ChainBuilder rememberFirstId() {
        return doIf(session -> !session.contains("mourabahaParametersId")).then(exec(
            http("GET /api/v1/mourabaha-parameters (first record)")
                .get("/api/v1/mourabaha-parameters")
                .queryParam("page", "1")
                .queryParam("size", "1")
                .check(status().is(200))
                .check(jsonPath("$.items[0].id").saveAs("mourabahaParametersId"))
        ));
    }

    public static ChainBuilder getById() {
        return rememberFirstId().exec(
            http("GET /api/v1/mourabaha-parameters/{id}")
                .get("/api/v1/mourabaha-parameters/#{mourabahaParametersId}")
                .check(status().is(200))
        );
    }

    public static ChainBuilder patchRates() {
        return exec(
            http("PATCH /api/v1/mourabaha-parameters/{id}")
                .patch("/api/v1/mourabaha-parameters/#{mourabahaParametersId}")
                .body(StringBody("{\"profitRate\":#{profitRate},\"vatRate\":#{vatRate}}"))
                .check(status().is(204))
        );
    }
}
