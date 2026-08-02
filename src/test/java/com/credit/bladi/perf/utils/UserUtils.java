package com.credit.bladi.perf.utils;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.http.HttpDsl.header;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.ChainBuilder;

public class UserUtils {

    private static final long ADMIN_ID = 1L;

    public static ChainBuilder getAll() {
        return exec(
            http("GET /api/v1/users")
                .get("/api/v1/users")
                .queryParam("page", "#{page}")
                .queryParam("size", "#{size}")
                .check(status().is(200))
        );
    }

    public static ChainBuilder getById() {
        return exec(
            http("GET /api/v1/users/{id}")
                .get("/api/v1/users/" + ADMIN_ID)
                .check(status().is(200))
        );
    }

    public static ChainBuilder create() {
        return exec(session -> session.set("newUsername",
                session.getString("usernamePrefix") + "." + UniqueUsernames.suffix(session.userId())))
            .exec(
                http("POST /api/v1/users")
                    .post("/api/v1/users")
                    .body(StringBody(
                        "{\"username\":\"#{newUsername}\",\"password\":\"#{password}\",\"role\":\"#{role}\"}"))
                    .check(status().is(201))
                    .check(header("Location").saveAs("newUserLocation"))
            );
    }

    public static ChainBuilder updateRoleOfCreatedUser() {
        return exec(session -> {
            String location = session.getString("newUserLocation");
            String id = location.substring(location.lastIndexOf('/') + 1);
            return session.set("newUserId", id);
        })
            .exec(
                http("PATCH /api/v1/users/{id}/role")
                    .patch("/api/v1/users/#{newUserId}/role")
                    .body(StringBody("{\"role\":\"MANAGER\"}"))
                    .check(status().is(204))
            );
    }
}
