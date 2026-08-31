package com.credit.bladi.perf.utils;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.ChainBuilder;

public class AuditLogUtils {
    public static ChainBuilder getAll() {
        return exec(
            http("GET /api/v1/audit-log")
                .get("/api/v1/audit-log")
                .queryParam("page", "#{page}")
                .queryParam("size", "#{size}")
                .check(status().is(200))
        );
    }
}
