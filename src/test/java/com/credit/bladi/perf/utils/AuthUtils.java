package com.credit.bladi.perf.utils;

import static com.credit.bladi.perf.Config.ADMIN_PASSWORD;
import static com.credit.bladi.perf.Config.ADMIN_USERNAME;
import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.ChainBuilder;

public class AuthUtils {

    private static final String LOGIN_ENDPOINT = "/api/v1/auth/login";

    public static ChainBuilder login() {
        return doLogin(
            "POST /api/v1/auth/login [#{username}]",
            "{\"username\":\"#{username}\",\"password\":\"#{password}\"}"
        );
    }

    public static ChainBuilder loginAsAdmin() {
        return doLogin(
            "POST /api/v1/auth/login (admin)",
            "{\"username\":\"%s\",\"password\":\"%s\"}".formatted(ADMIN_USERNAME, ADMIN_PASSWORD)
        );
    }

    private static ChainBuilder doLogin(String requestName, String body) {
        return exec(
            http(requestName)
                .post(LOGIN_ENDPOINT)
                .body(StringBody(body))
                .check(status().is(200))
        );
    }

    public static ChainBuilder register() {
        return exec(session -> session
                .set("regUsername", "perf.reg." + UniqueUsernames.suffix(session.userId()))
                .set("regPassword", "PerfLoad2026Aa1"))
            .exec(
                http("POST /api/v1/auth/register")
                    .post("/api/v1/auth/register")
                    .body(StringBody("{\"username\":\"#{regUsername}\",\"password\":\"#{regPassword}\"}"))
                    .check(status().is(201))
            );
    }

    public static ChainBuilder refresh() {
        return exec(
            http("POST /api/v1/auth/refresh")
                .post("/api/v1/auth/refresh")
                .check(status().is(200))
        );
    }

    public static ChainBuilder logout() {
        return exec(
            http("POST /api/v1/auth/logout")
                .post("/api/v1/auth/logout")
                .check(status().is(204))
        );
    }

    public static ChainBuilder me() {
        return exec(
            http("GET /api/v1/auth/me")
                .get("/api/v1/auth/me")
                .check(status().is(200))
        );
    }
}