package com.credit.bladi.infrastructure.sms;

import static com.credit.bladi.common.PiiMasker.maskPhone;

import com.credit.bladi.service.notification.sms.ISmsGateway;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "sms.gateway", name = "provider", havingValue = "http")
public class HttpSmsGateway implements ISmsGateway {
    private static final Logger LOGGER = LogManager.getLogger(HttpSmsGateway.class);

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(REQUEST_TIMEOUT).build();

    private final String gatewayUrl;

    private final String apiKey;

    private final String senderId;

    public HttpSmsGateway(
            @Value("${sms.gateway.url}") String gatewayUrl,
            @Value("${sms.gateway.api-key}") String apiKey,
            @Value("${sms.gateway.sender-id:CreditBladi}") String senderId) {
        this.gatewayUrl = gatewayUrl;
        this.apiKey = apiKey;
        this.senderId = senderId;
    }

    @Override
    public void send(String phoneNumber, String text) {
        String body = "{\"to\":\"" + escape(phoneNumber) + "\",\"from\":\"" + escape(senderId)
            + "\",\"message\":\"" + escape(text) + "\"}";
        HttpRequest request = HttpRequest.newBuilder(URI.create(gatewayUrl))
            .timeout(REQUEST_TIMEOUT)
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
            .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                LOGGER.info("SMS_GATEWAY_SENT provider=http to={} status={}", maskPhone(phoneNumber),
                    response.statusCode());
            } else {
                LOGGER.error("SMS_GATEWAY_FAILURE provider=http to={} status={} body={}", maskPhone(phoneNumber),
                    response.statusCode(), response.body());
            }
        } catch (IOException e) {
            LOGGER.error("SMS_GATEWAY_FAILURE provider=http to={}", maskPhone(phoneNumber), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("SMS_GATEWAY_FAILURE provider=http to={} reason=interrupted", maskPhone(phoneNumber));
        }
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
