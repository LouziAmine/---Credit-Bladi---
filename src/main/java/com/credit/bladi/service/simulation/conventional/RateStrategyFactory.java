package com.credit.bladi.service.simulation.conventional;

import static com.credit.bladi.exception.SimulationException.SimulationExceptionType.STRATEGY_NOT_FOUND;

import com.credit.bladi.exception.SimulationException;
import com.credit.bladi.model.enums.CspCode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class RateStrategyFactory {

    private final Map<CspCode, RateCalculationStrategy> strategies;

    public RateStrategyFactory(List<RateCalculationStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(RateCalculationStrategy::getCspCode, Function.identity()));
    }

    public RateCalculationStrategy getStrategy(CspCode cspCode) {
        RateCalculationStrategy strategy = strategies.get(cspCode);
        if (strategy == null) {
            throw SimulationException.of(STRATEGY_NOT_FOUND);
        }
        return strategy;
    }
}