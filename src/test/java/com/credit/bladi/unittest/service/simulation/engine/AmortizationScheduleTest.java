package com.credit.bladi.unittest.service.simulation.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.simulation.AmortizationRow;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.service.simulation.engine.RateCalculationEngine;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AmortizationScheduleTest {

    private RateCalculationEngine engine;

    @BeforeEach
    void setUp() {
        engine = new RateCalculationEngine();
    }

    @Test
    void shouldGenerateScheduleReconcilingWithBamAnnuityFixture() {
        Money capital = Money.of(new BigDecimal("500000"));
        Rate annualRate = Rate.of(new BigDecimal("5.5"));
        int months = 240;
        Money monthlyPayment = engine.calculateMonthlyPayment(capital, annualRate, months);
        BigDecimal monthlyRate = engine.calculateMonthlyRate(annualRate);

        List<AmortizationRow> schedule = engine.generateAmortizationSchedule(
            capital, monthlyRate, monthlyPayment, months);

        assertEquals(months, schedule.size());

        AmortizationRow first = schedule.get(0);
        assertEquals(1, first.installmentNumber());
        assertEquals(0, first.beginningBalance().amount().compareTo(capital.amount()));

        AmortizationRow last = schedule.get(months - 1);
        assertEquals(months, last.installmentNumber());
        assertEquals(0, last.endingBalance().amount().compareTo(new BigDecimal("0.00")));

        BigDecimal sumPrincipal = schedule.stream()
            .map(row -> row.principal().amount())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, sumPrincipal.compareTo(capital.amount()));

        BigDecimal sumPayment = schedule.stream()
            .map(row -> row.payment().amount())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal expectedTotal = capital.amount().add(new BigDecimal("325465.60"));
        assertTrue(sumPayment.subtract(expectedTotal).abs().compareTo(new BigDecimal("5.00")) <= 0);

        for (int i = 0; i < schedule.size(); i++) {
            AmortizationRow row = schedule.get(i);
            BigDecimal expectedEndingBalance = row.beginningBalance().amount().subtract(row.principal().amount());
            assertEquals(0, row.endingBalance().amount().compareTo(expectedEndingBalance));
            if (i < schedule.size() - 1) {
                assertEquals(0, row.endingBalance().amount()
                    .compareTo(schedule.get(i + 1).beginningBalance().amount()));
            }
        }
    }
}
