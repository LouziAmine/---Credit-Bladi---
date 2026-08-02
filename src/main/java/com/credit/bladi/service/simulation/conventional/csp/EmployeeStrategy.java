package com.credit.bladi.service.simulation.conventional.csp;

import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.simulation.CreditRateModel;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.service.simulation.conventional.RateCalculationStrategy;
import org.springframework.stereotype.Component;

@Component
public class EmployeeStrategy implements RateCalculationStrategy {

    @Override
    public CspCode getCspCode() {
        return CspCode.SALA;
    }

    @Override
    public Rate calculateRate(CreditRateModel creditRate) {
        return creditRate.getRateMin().average(creditRate.getRateMax());
    }
}