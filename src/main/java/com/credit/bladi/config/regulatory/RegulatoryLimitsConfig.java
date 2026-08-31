package com.credit.bladi.config.regulatory;

import com.credit.bladi.model.parameters.RateBasis;
import com.credit.bladi.model.parameters.RegulatoryLimits;
import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegulatoryLimitsConfig {
    @Bean
    public RegulatoryLimits regulatoryLimits(
            @Value("${credit.bladi.regulatory.max-annual-rate}") BigDecimal maxAnnualRate,
            @Value("${credit.bladi.regulatory.simulation-rate-basis:NOMINAL}") RateBasis simulationBasis) {
        return new RegulatoryLimits(Rate.of(maxAnnualRate), simulationBasis);
    }
}
