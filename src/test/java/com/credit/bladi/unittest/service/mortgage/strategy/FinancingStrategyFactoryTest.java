package com.credit.bladi.unittest.service.mortgage.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.credit.bladi.exception.MortgageCreditException;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.service.mortgage.strategy.FinancingCalculationStrategy;
import com.credit.bladi.service.mortgage.strategy.FinancingResult;
import com.credit.bladi.service.mortgage.strategy.FinancingStrategyFactory;
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

        assertThrows(MortgageCreditException.class, () -> emptyFactory.getStrategy(ProductType.CONVENTIONAL));
    }

    private static FinancingCalculationStrategy stub(ProductType productType) {
        return new FinancingCalculationStrategy() {
            @Override
            public ProductType getProductType() {
                return productType;
            }

            @Override
            public FinancingResult simulate(com.credit.bladi.model.mortgage.request.MortgageCreditRequest request,
                                            com.credit.bladi.model.bam.BamParametersModel bamParameters,
                                            com.credit.bladi.model.vo.Money totalMonthlyIncome) {
                throw new UnsupportedOperationException("not used in this test");
            }

            @Override
            public void assertEligible(com.credit.bladi.model.workflow.MortgageDemandDetail detail,
                    com.credit.bladi.model.vo.Money amount, com.credit.bladi.model.vo.Rate rate, int duration,
                    com.credit.bladi.model.bam.BamParametersModel bamParameters) {
                throw new UnsupportedOperationException("not used in this test");
            }

            @Override
            public com.credit.bladi.model.mortgage.MortgageAmortizationSnapshot computeAmortizationSchedule(
                    com.credit.bladi.model.workflow.DemandModel demand, com.credit.bladi.model.vo.Money amount,
                    int duration, com.credit.bladi.model.vo.Rate rateOverride,
                    com.credit.bladi.model.vo.Money propertyValue,
                    com.credit.bladi.model.bam.BamParametersModel bamParameters, boolean coBorrower) {
                throw new UnsupportedOperationException("not used in this test");
            }
        };
    }
}
