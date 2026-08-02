package com.credit.bladi.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.credit.bladi.integration.tools.IntegrationToken;
import io.karatelabs.core.FeatureResult;
import io.karatelabs.core.Runner;
import io.karatelabs.core.ScenarioResult;
import io.karatelabs.core.SuiteResult;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class IntegrationTest {

    private static final Logger LOGGER = LogManager.getLogger(IntegrationTest.class);
    private static final String BASE_URL = "http://localhost:8080";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    @Test
    public void sequentialIntegrationTests() {
        LOGGER.debug("Begin Karate integration tests");
        Locale.setDefault(Locale.ENGLISH);
        SuiteResult results = Runner.path("classpath:.")
            .systemProperty("karate.baseUrl", BASE_URL)
            .systemProperty("karate.adminUsername", ADMIN_USERNAME)
            .systemProperty("karate.adminPassword", ADMIN_PASSWORD)
            .systemProperty("karate.managerToken",
                IntegrationToken.getManagerToken(BASE_URL, ADMIN_USERNAME, ADMIN_PASSWORD))
            .parallel(1);

        if (results.getFeatureFailedCount() != 0) {
            results.getFeatureResults().stream()
                .filter(FeatureResult::isFailed)
                .forEach(fr -> {
                    LOGGER.error("Feature FAILED: {}", fr.getFeature().getNameForReport());
                    fr.getScenarioResults().stream()
                        .filter(ScenarioResult::isFailed)
                        .forEach(sr -> {
                            LOGGER.error("  Scenario : {}", sr.getScenario().getName());
                            LOGGER.error("  Step     : {}", sr.getFailedStepText());
                            LOGGER.error("  Error    : {}", sr.getFailureMessage());
                        });
                });
        }
        assertEquals(0, results.getFeatureFailedCount(),
            results.getFeatureFailedCount() + " Karate feature(s) failed — see target/karate-reports/");
    }
}