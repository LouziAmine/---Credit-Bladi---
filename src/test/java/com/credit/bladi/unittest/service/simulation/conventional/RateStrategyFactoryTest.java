package com.credit.bladi.unittest.service.simulation.conventional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.credit.bladi.exception.SimulationException;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.service.simulation.conventional.RateCalculationStrategy;
import com.credit.bladi.service.simulation.conventional.RateStrategyFactory;
import com.credit.bladi.service.simulation.conventional.csp.CivilServantStrategy;
import com.credit.bladi.service.simulation.conventional.csp.EmployeeStrategy;
import com.credit.bladi.service.simulation.conventional.csp.FreelancerStrategy;
import com.credit.bladi.service.simulation.conventional.csp.ProfessionalStrategy;
import com.credit.bladi.service.simulation.conventional.csp.RetiredStrategy;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RateStrategyFactoryTest {

    private RateStrategyFactory factory;

    @BeforeEach
    void setUp() {
        factory = new RateStrategyFactory(List.of(
            new CivilServantStrategy(),
            new EmployeeStrategy(),
            new FreelancerStrategy(),
            new ProfessionalStrategy(),
            new RetiredStrategy()
        ));
    }

    @Test
    void shouldResolveStrategyForSala() {
        RateCalculationStrategy strategy = factory.getStrategy(CspCode.SALA);
        assertNotNull(strategy);
        assertEquals(CspCode.SALA, strategy.getCspCode());
        assertInstanceOf(EmployeeStrategy.class, strategy);
    }

    @Test
    void shouldResolveStrategyForFonc() {
        RateCalculationStrategy strategy = factory.getStrategy(CspCode.FONC);
        assertNotNull(strategy);
        assertEquals(CspCode.FONC, strategy.getCspCode());
        assertInstanceOf(CivilServantStrategy.class, strategy);
    }

    @Test
    void shouldResolveStrategyForInde() {
        RateCalculationStrategy strategy = factory.getStrategy(CspCode.INDE);
        assertNotNull(strategy);
        assertEquals(CspCode.INDE, strategy.getCspCode());
        assertInstanceOf(FreelancerStrategy.class, strategy);
    }

    @Test
    void shouldResolveStrategyForProf() {
        RateCalculationStrategy strategy = factory.getStrategy(CspCode.PROF);
        assertNotNull(strategy);
        assertEquals(CspCode.PROF, strategy.getCspCode());
        assertInstanceOf(ProfessionalStrategy.class, strategy);
    }

    @Test
    void shouldResolveStrategyForRetr() {
        RateCalculationStrategy strategy = factory.getStrategy(CspCode.RETR);
        assertNotNull(strategy);
        assertEquals(CspCode.RETR, strategy.getCspCode());
        assertInstanceOf(RetiredStrategy.class, strategy);
    }

    @Test
    void shouldThrowSimulationExceptionWhenStrategyNotFound() {
        RateStrategyFactory emptyFactory = new RateStrategyFactory(List.of());

        assertThrows(SimulationException.class, () -> emptyFactory.getStrategy(CspCode.SALA));
    }
}