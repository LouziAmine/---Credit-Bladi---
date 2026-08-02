package com.credit.bladi.perf.utils;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.ChainBuilder;

public class BamParametersUtils {

    private static final long SEEDED_ID = 1L;

    public static ChainBuilder getAllParameters() {
        return exec(
            http("GET /api/v1/bam-parameters")
                .get("/api/v1/bam-parameters")
                .queryParam("page", "#{page}")
                .queryParam("size", "#{size}")
                .check(status().is(200))
        );
    }

    public static ChainBuilder getById() {
        return exec(
            http("GET /api/v1/bam-parameters/{id}")
                .get("/api/v1/bam-parameters/" + SEEDED_ID)
                .check(status().is(200))
        );
    }

    public static ChainBuilder patchFees() {
        return exec(
            http("PATCH /api/v1/bam-parameters/{id}")
                .patch("/api/v1/bam-parameters/" + SEEDED_ID)
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
