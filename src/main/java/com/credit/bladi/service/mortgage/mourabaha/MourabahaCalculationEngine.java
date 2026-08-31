package com.credit.bladi.service.mortgage.mourabaha;

import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.mourabaha.MourabahaAmortizationRow;
import com.credit.bladi.model.mourabaha.MourabahaFinancialSnapshot;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import com.credit.bladi.model.vo.AmortizationRow;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.service.calculation.RateCalculationEngine;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MourabahaCalculationEngine {
    private final RateCalculationEngine rateEngine;

    public MourabahaCalculationEngine(RateCalculationEngine rateEngine) {
        this.rateEngine = rateEngine;
    }

    public MourabahaFinancialSnapshot compute(Money capital, Money propertyValue,
                                              MourabahaParametersModel mourabahaParameters,
                                              int months, BamParametersModel bamParameters,
                                              boolean coBorrower) {
        BigDecimal vatRateValue = mourabahaParameters.getVatRate().value();
        BigDecimal ttcRateValue = mourabahaParameters.getProfitRate().value()
            .multiply(BigDecimal.ONE.add(vatRateValue));
        Rate ttcRate = Rate.of(ttcRateValue);

        Money monthlyPaymentTtc = rateEngine.calculateMonthlyPayment(capital, ttcRate, months);
        Money marginTtc = rateEngine.calculateTotalInterestCost(monthlyPaymentTtc, months, capital);
        Money marginHt = Money.of(
            marginTtc.amount().divide(BigDecimal.ONE.add(vatRateValue), 2, RoundingMode.HALF_UP));
        Money vatOnMargin = Money.of(marginTtc.amount().subtract(marginHt.amount()));
        Money totalCostTtc = capital.add(marginTtc);

        Money registrationFees = rateEngine.calculateRegistrationFees(propertyValue, bamParameters);
        Money landConservationFees = rateEngine.calculateLandConservationFees(propertyValue, bamParameters);
        Money notaryFees = rateEngine.calculateNotaryFees(propertyValue, bamParameters);
        Money processingFees = rateEngine.calculateProcessingFees(capital, bamParameters);
        Money totalFees = registrationFees.add(landConservationFees).add(notaryFees).add(processingFees);

        Money takafulMonthlyContribution = calculateTakafulMonthlyContribution(
            capital, mourabahaParameters, coBorrower);
        Money allInMonthlyPaymentTtc = monthlyPaymentTtc.add(takafulMonthlyContribution);
        Money totalTakafulCost = rateEngine.calculateTotalAdiCost(takafulMonthlyContribution, months);

        return new MourabahaFinancialSnapshot(
            monthlyPaymentTtc, marginHt, vatOnMargin, marginTtc, totalCostTtc,
            registrationFees, landConservationFees, notaryFees, processingFees, totalFees,
            takafulMonthlyContribution, allInMonthlyPaymentTtc, totalTakafulCost
        );
    }

    private static Money calculateTakafulMonthlyContribution(Money capital, MourabahaParametersModel params,
                                                              boolean coBorrower) {
        BigDecimal multiplier = coBorrower ? BigDecimal.valueOf(2) : BigDecimal.ONE;
        BigDecimal result = capital.amount()
            .multiply(params.getTakafulMonthlyRate().value())
            .multiply(multiplier)
            .setScale(2, RoundingMode.HALF_UP);
        return Money.of(result);
    }

    public List<MourabahaAmortizationRow> computeSchedule(Money capital,
                                                          MourabahaParametersModel mourabahaParameters,
                                                          int months) {
        BigDecimal vatRateValue = mourabahaParameters.getVatRate().value();
        BigDecimal ttcRateValue = mourabahaParameters.getProfitRate().value()
            .multiply(BigDecimal.ONE.add(vatRateValue));
        Rate ttcRate = Rate.of(ttcRateValue);

        BigDecimal monthlyRate = rateEngine.calculateMonthlyRate(ttcRate);
        Money monthlyPaymentTtc = rateEngine.calculateMonthlyPayment(capital, ttcRate, months);

        return rateEngine.generateAmortizationSchedule(capital, monthlyRate, monthlyPaymentTtc, months)
            .stream()
            .map(row -> toMourabahaRow(row, vatRateValue))
            .toList();
    }

    private static MourabahaAmortizationRow toMourabahaRow(AmortizationRow row, BigDecimal vatRateValue) {
        Money marginTtc = row.interest();
        Money marginHt = Money.of(
            marginTtc.amount().divide(BigDecimal.ONE.add(vatRateValue), 2, RoundingMode.HALF_UP));
        Money vatOnMargin = marginTtc.subtract(marginHt);
        return new MourabahaAmortizationRow(row.installmentNumber(), row.beginningBalance(),
            marginHt, vatOnMargin, marginTtc, row.principal(), row.payment(), row.endingBalance());
    }
}
