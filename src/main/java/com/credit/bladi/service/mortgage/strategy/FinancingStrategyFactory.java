package com.credit.bladi.service.mortgage.strategy;

import static com.credit.bladi.exception.MortgageCreditException.MortgageCreditExceptionType.STRATEGY_NOT_FOUND;

import com.credit.bladi.exception.MortgageCreditException;
import com.credit.bladi.model.enums.ProductType;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class FinancingStrategyFactory {
    private final Map<ProductType, FinancingCalculationStrategy> strategies;

    public FinancingStrategyFactory(List<FinancingCalculationStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(FinancingCalculationStrategy::getProductType, Function.identity()));
    }

    public FinancingCalculationStrategy getStrategy(ProductType productType) {
        FinancingCalculationStrategy strategy = strategies.get(productType);
        if (strategy == null) {
            throw MortgageCreditException.of(STRATEGY_NOT_FOUND);
        }
        return strategy;
    }
}
