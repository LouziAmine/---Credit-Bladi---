package com.credit.bladi.service.investment;

import com.credit.bladi.model.investment.InvestmentFinancialSnapshot;
import com.credit.bladi.model.investment.request.InvestmentCreditRequest;
import com.credit.bladi.model.investment.response.InvestmentCreditResponse;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;

public interface IInvestmentCreditService {
    InvestmentCreditResponse simulate(InvestmentCreditRequest request);

    InvestmentFinancialSnapshot getAmortizationSchedule(Long demandId, Money amountOverride, Rate rateOverride,
            Integer durationOverride);
}
