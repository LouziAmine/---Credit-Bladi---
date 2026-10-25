package com.credit.bladi.service.mortgage.strategy;

import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;

public sealed interface FinancingResult permits ConventionalFinancingResult, MourabahaFinancingResult {
    Rate applicableRate();

    BigDecimal taeg();
}
