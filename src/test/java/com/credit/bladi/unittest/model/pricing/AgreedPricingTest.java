package com.credit.bladi.unittest.model.pricing;

import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_0100;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_0150;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_0300;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_0400;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_4000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_9000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_10000_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_1000_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_25;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_0_0004;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_0_0009;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_0_0080;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_0_0100;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_0_1000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_0_2000;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_DECIDER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import com.credit.bladi.model.pricing.AgreedPricing;
import com.credit.bladi.model.pricing.AgreedPricing.MortgageFees;
import com.credit.bladi.model.pricing.AgreedPricing.MourabahaTerms;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AgreedPricingTest {
    private static BamParametersModel bam(String adiMonthlyRate, String processingFeesRatio) {
        return BamParametersModel.getBuilder().id(1L).publicId(UUID.randomUUID())
            .processingFeesRatio(Ratio.of(new BigDecimal(processingFeesRatio)))
            .minProcessingFees(Money.of(DECIMAL_1000_00))
            .maxProcessingFees(Money.of(DECIMAL_10000_00))
            .registrationFeeRatio(Ratio.of(DECIMAL_0_0400))
            .landConservationRatio(Ratio.of(DECIMAL_0_0150))
            .notaryFeeRatio(Ratio.of(DECIMAL_0_0100))
            .ltvRatio(Ratio.of(DECIMAL_0_9000))
            .maxDebtRatio(Ratio.of(DECIMAL_0_4000))
            .maxAgeAtMaturity(75)
            .adiMonthlyRate(Ratio.of(new BigDecimal(adiMonthlyRate)))
            .active(true)
            .build();
    }

    private static MourabahaParametersModel mourabaha(String vatRate) {
        return MourabahaParametersModel.getBuilder().id(1L).publicId(UUID.randomUUID())
            .profitRate(Rate.of(DECIMAL_5_25))
            .vatRate(Ratio.of(new BigDecimal(vatRate)))
            .takafulMonthlyRate(Rate.of(DECIMAL_0_0300))
            .active(true)
            .build();
    }

    @Test
    void shouldKeepAgreedFeesAndInsuranceWhenTheGridChangesAfterTheDecision() {
        BamParametersModel atDecision = bam(TEXT_0_0004, TEXT_0_0100);
        AgreedPricing agreed = new AgreedPricing(7L, MortgageFees.of(atDecision), null, null, Instant.EPOCH,
            USER_DECIDER);
        BamParametersModel editedLater = bam(TEXT_0_0009, TEXT_0_0080);

        BamParametersModel applied = agreed.applyTo(editedLater);

        assertEquals(atDecision.getAdiMonthlyRate(), applied.getAdiMonthlyRate());
        assertEquals(atDecision.getProcessingFeesRatio(), applied.getProcessingFeesRatio());
        assertEquals(editedLater.getLtvRatio(), applied.getLtvRatio());
    }

    @Test
    void shouldKeepAgreedMourabahaVatWhenTheGridChangesAfterTheDecision() {
        MourabahaParametersModel atDecision = mourabaha(TEXT_0_1000);
        AgreedPricing agreed = new AgreedPricing(7L, null, MourabahaTerms.of(atDecision), null, Instant.EPOCH,
            USER_DECIDER);

        MourabahaParametersModel applied = agreed.applyTo(mourabaha(TEXT_0_2000));

        assertEquals(atDecision.getVatRate(), applied.getVatRate());
        assertEquals(atDecision.getTakafulMonthlyRate(), applied.getTakafulMonthlyRate());
    }

    @Test
    void shouldLeaveTheActiveGridUntouchedWhenNothingWasFrozenForIt() {
        AgreedPricing agreed = new AgreedPricing(7L, null, null, null, Instant.EPOCH, USER_DECIDER);
        BamParametersModel active = bam(TEXT_0_0004, TEXT_0_0100);

        assertSame(active, agreed.applyTo(active));
    }
}
