package com.credit.bladi.config.servicing;

import com.credit.bladi.model.servicing.PaymentBeneficiary;
import com.credit.bladi.model.vo.Rib;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentBeneficiaryConfig {
    @Bean
    public PaymentBeneficiary paymentBeneficiary(
            @Value("${credit.bladi.payment.beneficiary-name}") String name,
            @Value("${credit.bladi.payment.beneficiary-rib}") String rib) {
        return new PaymentBeneficiary(name, Rib.of(rib));
    }
}
