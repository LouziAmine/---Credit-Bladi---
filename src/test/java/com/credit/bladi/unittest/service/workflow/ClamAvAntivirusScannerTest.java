package com.credit.bladi.unittest.service.workflow;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.infrastructure.antivirus.ClamAvAntivirusScanner;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class ClamAvAntivirusScannerTest {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @AfterEach
    void tearDown() {
        executor.shutdownNow();
    }

    private Future<Void> fakeClamdRespondingWith(ServerSocket serverSocket, String response) {
        return executor.submit((Callable<Void>) () -> {
            try (Socket accepted = serverSocket.accept()) {
                accepted.getInputStream().readAllBytes();
                accepted.getOutputStream().write(response.getBytes(StandardCharsets.US_ASCII));
                accepted.getOutputStream().flush();
            }
            return null;
        });
    }

    @Test
    void shouldReturnFalseWhenDaemonReportsClean() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            Future<Void> serverTask = fakeClamdRespondingWith(serverSocket, "stream: OK\0");
            ClamAvAntivirusScanner scanner = new ClamAvAntivirusScanner("localhost", serverSocket.getLocalPort(),
                2000);

            boolean infected = scanner.isInfected("hello world".getBytes(StandardCharsets.UTF_8));

            serverTask.get(5, TimeUnit.SECONDS);
            assertFalse(infected);
        }
    }

    @Test
    void shouldReturnTrueWhenDaemonReportsFound() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            Future<Void> serverTask = fakeClamdRespondingWith(serverSocket,
                "stream: Win.Test.EICAR_HDB-1 FOUND\0");
            ClamAvAntivirusScanner scanner = new ClamAvAntivirusScanner("localhost", serverSocket.getLocalPort(),
                2000);

            boolean infected = scanner.isInfected("fake eicar payload".getBytes(StandardCharsets.UTF_8));

            serverTask.get(5, TimeUnit.SECONDS);
            assertTrue(infected);
        }
    }

    @Test
    void shouldFailClosedWhenDaemonIsUnreachable() throws IOException {
        int unusedPort;
        try (ServerSocket probe = new ServerSocket(0)) {
            unusedPort = probe.getLocalPort();
        }

        ClamAvAntivirusScanner scanner = new ClamAvAntivirusScanner("localhost", unusedPort, 1000);

        boolean infected = scanner.isInfected("any content".getBytes(StandardCharsets.UTF_8));

        assertTrue(infected, "an unreachable clamd must fail closed (treated as infected)");
    }
}
