package com.credit.bladi.perf.utils;

import static io.gatling.javaapi.core.CoreDsl.exec;

import io.gatling.javaapi.core.ChainBuilder;

public final class ClientIpUtils {
    private static final String[] BLOCKS = {"192.0.2", "198.51.100", "203.0.113"};
    private static final int HOSTS_PER_BLOCK = 254;

    private ClientIpUtils() {
    }

    public static ChainBuilder assignSyntheticClientIp() {
        return exec(session -> {
            int n = Math.floorMod(session.userId(), BLOCKS.length * HOSTS_PER_BLOCK);
            String block = BLOCKS[n / HOSTS_PER_BLOCK];
            int host = (n % HOSTS_PER_BLOCK) + 1;
            return session.set("clientIp", block + "." + host);
        });
    }
}
