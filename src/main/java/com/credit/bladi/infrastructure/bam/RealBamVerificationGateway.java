package com.credit.bladi.infrastructure.bam;

import com.credit.bladi.model.enums.BamScenario;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.verification.BamVerificationResult;
import com.credit.bladi.service.verification.bam.IBamVerificationGateway;
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
@ConditionalOnProperty(prefix = "bam.verification", name = "provider", havingValue = "real")
public class RealBamVerificationGateway implements IBamVerificationGateway {
    private static final Logger LOGGER = LogManager.getLogger(RealBamVerificationGateway.class);

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(15);

    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(REQUEST_TIMEOUT).build();

    private final String gatewayUrl;

    private final String apiKey;

    public RealBamVerificationGateway(
            @Value("${bam.verification.gateway.url}") String gatewayUrl,
            @Value("${bam.verification.gateway.api-key}") String apiKey) {
        this.gatewayUrl = gatewayUrl;
        this.apiKey = apiKey;
    }

    @Override
    public BamVerificationResult verify(EnterpriseType enterpriseType, String identifier, String companyName,
            BamScenario forcedScenario) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(gatewayUrl))
            .timeout(REQUEST_TIMEOUT)
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(
                buildRequestBody(enterpriseType, identifier, companyName), StandardCharsets.UTF_8))
            .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                LOGGER.error("BAM_VERIFICATION_GATEWAY_FAILURE status={} body={}", response.statusCode(),
                    response.body());
                throw new IllegalStateException(
                    "BAM verification gateway returned status " + response.statusCode());
            }
            return parseResponse(response.body());
        } catch (IOException e) {
            LOGGER.error("BAM_VERIFICATION_GATEWAY_FAILURE identifier={}", identifier, e);
            throw new IllegalStateException("BAM verification gateway call failed", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("BAM verification gateway call interrupted", e);
        }
    }

    private static String buildRequestBody(EnterpriseType enterpriseType, String identifier,
            String companyName) {
        return "{\"enterpriseType\":\"" + enterpriseType + "\",\"identifier\":\"" + escape(identifier)
            + "\",\"companyName\":\"" + escape(companyName) + "\"}";
    }

    private static BamVerificationResult parseResponse(String body) {
        throw new UnsupportedOperationException(
            "RealBamVerificationGateway#parseResponse is a placeholder: implement it against the "
                + "chosen provider's actual response contract before enabling bam.verification.provider=real.");
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
