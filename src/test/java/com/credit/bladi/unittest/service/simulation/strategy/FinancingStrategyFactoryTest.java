package com.credit.bladi.unittest.service.simulation.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.credit.bladi.exception.SimulationException;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.service.simulation.strategy.FinancingCalculationStrategy;
import com.credit.bladi.service.simulation.strategy.FinancingResult;
import com.credit.bladi.service.simulation.strategy.FinancingStrategyFactory;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FinancingStrategyFactoryTest {

    private FinancingStrategyFactory factory;

    @BeforeEach
    void setUp() {
        factory = new FinancingStrategyFactory(List.of(
            stub(ProductType.CONVENTIONAL),
            stub(ProductType.MOURABAHA)
        ));
    }

    @Test
    void shouldResolveStrategyForConventional() {
        FinancingCalculationStrategy strategy = factory.getStrategy(ProductType.CONVENTIONAL);
        assertNotNull(strategy);
        assertEquals(ProductType.CONVENTIONAL, strategy.getProductType());
    }

    @Test
    void shouldResolveStrategyForMourabaha() {
        FinancingCalculationStrategy strategy = factory.getStrategy(ProductType.MOURABAHA);
        assertNotNull(strategy);
        assertEquals(ProductType.MOURABAHA, strategy.getProductType());
    }

    @Test
    void shouldThrowSimulationExceptionWhenStrategyNotFound() {
        FinancingStrategyFactory emptyFactory = new FinancingStrategyFactory(List.of());

        assertThrows(SimulationException.class, () -> emptyFactory.getStrategy(ProductType.CONVENTIONAL));
    }

    private static FinancingCalculationStrategy stub(ProductType productType) {
        return new FinancingCalculationStrategy() {
            @Override
            public ProductType getProductType() {
                return productType;
            }

            @Override
            public FinancingResult simulate(com.credit.bladi.model.simulation.request.SimulationRequest request,
                                            com.credit.bladi.model.bam.BamParametersModel bamParameters,
                                            com.credit.bladi.model.vo.Money totalMonthlyIncome) {
                throw new UnsupportedOperationException("not used in this test");
            }
        };
    }
}
