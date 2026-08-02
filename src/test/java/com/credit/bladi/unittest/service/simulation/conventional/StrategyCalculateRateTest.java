package com.credit.bladi.unittest.service.simulation.conventional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.credit.bladi.model.simulation.CreditRateModel;
import com.credit.bladi.model.vo.DurationRange;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.service.simulation.conventional.csp.CivilServantStrategy;
import com.credit.bladi.service.simulation.conventional.csp.EmployeeStrategy;
import com.credit.bladi.service.simulation.conventional.csp.FreelancerStrategy;
import com.credit.bladi.service.simulation.conventional.csp.ProfessionalStrategy;
import com.credit.bladi.service.simulation.conventional.csp.RetiredStrategy;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StrategyCalculateRateTest {

    private CreditRateModel creditRate;

    @BeforeEach
    void setUp() {
        creditRate = CreditRateModel.getBuilder()
            .id(1L)
            .cspCode(com.credit.bladi.model.enums.CspCode.SALA)
            .rateMin(Rate.of(new BigDecimal("3.50")))
            .rateMax(Rate.of(new BigDecimal("5.50")))
            .durationRange(DurationRange.of(12, 300))
            .active(true)
            .build();
    }

    @Test
    void civilServantStrategyReturnsRateMin() {
        CivilServantStrategy strategy = new CivilServantStrategy();
        Rate result = strategy.calculateRate(creditRate);
        assertEquals(0, result.value().compareTo(new BigDecimal("3.50")));
    }

    @Test
    void employeeStrategyReturnsAverageRate() {
        EmployeeStrategy strategy = new EmployeeStrategy();
        Rate result = strategy.calculateRate(creditRate);
        assertEquals(0, result.value().compareTo(new BigDecimal("4.5000")));
    }

    @Test
    void freelancerStrategyReturnsRateMax() {
        FreelancerStrategy strategy = new FreelancerStrategy();
        Rate result = strategy.calculateRate(creditRate);
        assertEquals(0, result.value().compareTo(new BigDecimal("5.50")));
    }

    @Test
    void professionalStrategyReturnsRateMax() {
        ProfessionalStrategy strategy = new ProfessionalStrategy();
        Rate result = strategy.calculateRate(creditRate);
        assertEquals(0, result.value().compareTo(new BigDecimal("5.50")));
    }

    @Test
    void retiredStrategyReturnsRateMin() {
        RetiredStrategy strategy = new RetiredStrategy();
        Rate result = strategy.calculateRate(creditRate);
        assertEquals(0, result.value().compareTo(new BigDecimal("3.50")));
    }
}