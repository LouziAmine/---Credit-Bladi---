package com.credit.bladi.unittest.service.notification.sms;

import static com.credit.bladi.unittest.mock.data.MockContacts.IP_127_0_0_1;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0612345678;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_AUTHORIZATION;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_BEARER_SECRET_KEY;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CREDITBLADI;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_VOTRE_DOSSIER_EST_EN_COURS_D_EXAMEN;
import static com.credit.bladi.unittest.mock.data.MockUsers.PASSWORD_SECRET_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.infrastructure.sms.HttpSmsGateway;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HttpSmsGatewayTest {
    private HttpServer server;

    private final AtomicReference<String> capturedBody = new AtomicReference<>();

    private final AtomicReference<String> capturedAuthHeader = new AtomicReference<>();

    private int statusToReturn = 200;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(IP_127_0_0_1, 0), 0);
        server.createContext("/sms", exchange -> {
            capturedBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            capturedAuthHeader.set(exchange.getRequestHeaders().getFirst(LABEL_AUTHORIZATION));
            byte[] response = "{}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(statusToReturn, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    private String gatewayUrl() {
        return "http://127.0.0.1:" + server.getAddress().getPort() + "/sms";
    }

    @Test
    void shouldPostJsonBodyWithBearerToken() {
        HttpSmsGateway gateway = new HttpSmsGateway(gatewayUrl(), PASSWORD_SECRET_KEY, LABEL_CREDITBLADI);

        gateway.send(PHONE_0612345678, LABEL_VOTRE_DOSSIER_EST_EN_COURS_D_EXAMEN);

        assertEquals(LABEL_BEARER_SECRET_KEY, capturedAuthHeader.get());
        assertTrue(capturedBody.get().contains("\"to\":\"0612345678\""));
        assertTrue(capturedBody.get().contains("\"from\":\"CreditBladi\""));
        assertTrue(capturedBody.get().contains(LABEL_VOTRE_DOSSIER_EST_EN_COURS_D_EXAMEN));
    }

    @Test
    void shouldEscapeQuotesAndBackslashesInTheMessage() {
        HttpSmsGateway gateway = new HttpSmsGateway(gatewayUrl(), PASSWORD_SECRET_KEY, LABEL_CREDITBLADI);

        gateway.send(PHONE_0612345678, "Dossier \"DEM-1\": statut: en cours \\ décision");

        assertTrue(capturedBody.get().contains("\\\"DEM-1\\\""));
        assertTrue(capturedBody.get().contains("\\\\"));
    }

    @Test
    void shouldNotThrowWhenGatewayReturnsAnErrorStatus() {
        statusToReturn = 500;
        HttpSmsGateway gateway = new HttpSmsGateway(gatewayUrl(), PASSWORD_SECRET_KEY, LABEL_CREDITBLADI);

        gateway.send(PHONE_0612345678, "text");
    }

    @Test
    void shouldNotThrowWhenGatewayIsUnreachable() {
        server.stop(0);
        HttpSmsGateway gateway = new HttpSmsGateway(gatewayUrl(), PASSWORD_SECRET_KEY, LABEL_CREDITBLADI);

        gateway.send(PHONE_0612345678, "text");
    }
}
