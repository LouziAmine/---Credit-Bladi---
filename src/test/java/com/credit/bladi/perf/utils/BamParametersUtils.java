package com.credit.bladi.perf.utils;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.doIf;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.ChainBuilder;

public class BamParametersUtils {
    public static ChainBuilder getAllParameters() {
        return exec(
            http("GET /api/v1/bam-parameters")
                .get("/api/v1/bam-parameters")
                .queryParam("page", "#{page}")
                .queryParam("size", "#{size}")
                .check(status().is(200))
                .check(jsonPath("$.items[0].id").optional().saveAs("bamParametersId"))
        );
    }

    // Ids are UUIDs: the detail and patch requests use the first listed record. A listed page past the
    // last one is empty, so the first page is read when no id is known yet.
    private static ChainBuilder rememberFirstId() {
        return doIf(session -> !session.contains("bamParametersId")).then(exec(
            http("GET /api/v1/bam-parameters (first record)")
                .get("/api/v1/bam-parameters")
                .queryParam("page", "1")
                .queryParam("size", "1")
                .check(status().is(200))
                .check(jsonPath("$.items[0].id").saveAs("bamParametersId"))
        ));
    }

    public static ChainBuilder getById() {
        return rememberFirstId().exec(
            http("GET /api/v1/bam-parameters/{id}")
                .get("/api/v1/bam-parameters/#{bamParametersId}")
                .check(status().is(200))
        );
    }

    public static ChainBuilder patchFees() {
        return exec(
            http("PATCH /api/v1/bam-parameters/{id}")
                .patch("/api/v1/bam-parameters/#{bamParametersId}")
                .body(StringBody(
                    "{"
                    + "\"processingFeesRatio\":#{processingFeesRatio},"
                    + "\"minProcessingFees\":#{minProcessingFees},"
                    + "\"maxProcessingFees\":#{maxProcessingFees},"
                    + "\"registrationFeeRatio\":#{registrationFeeRatio},"
                    + "\"landConservationRatio\":#{landConservationRatio},"
                    + "\"notaryFeeRatio\":#{notaryFeeRatio},"
                    + "\"adiMonthlyRate\":#{adiMonthlyRate}"
                    + "}"))
                .check(status().is(204))
        );
    }
}
