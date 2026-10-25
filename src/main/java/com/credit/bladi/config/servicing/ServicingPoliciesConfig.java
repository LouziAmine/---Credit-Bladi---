package com.credit.bladi.config.servicing;

import com.credit.bladi.model.servicing.EarlyRepaymentPolicy;
import com.credit.bladi.model.servicing.LateInterestPolicy;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServicingPoliciesConfig {
    @Bean
    public LateInterestPolicy lateInterestPolicy(
            @Value("${credit.bladi.servicing.late-interest-margin-points}") BigDecimal marginPoints) {
        return new LateInterestPolicy(marginPoints);
    }

    @Bean
    public EarlyRepaymentPolicy earlyRepaymentPolicy(
            @Value("${credit.bladi.servicing.early-repayment.indemnity-months-of-interest}") BigDecimal months,
            @Value("${credit.bladi.servicing.early-repayment.max-indemnity-share-of-outstanding}")
            BigDecimal maxShareOfOutstanding) {
        return new EarlyRepaymentPolicy(months, maxShareOfOutstanding);
    }
}
