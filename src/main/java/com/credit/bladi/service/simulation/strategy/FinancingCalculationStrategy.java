package com.credit.bladi.service.simulation.strategy;

import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.simulation.request.SimulationRequest;
import com.credit.bladi.model.vo.Money;

public interface FinancingCalculationStrategy {

    ProductType getProductType();

    FinancingResult simulate(SimulationRequest request, BamParametersModel bamParameters, Money totalMonthlyIncome);
}
