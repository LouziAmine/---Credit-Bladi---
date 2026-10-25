package com.credit.bladi.model.investment.response;

import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.investment.InvestmentFinancialSnapshot;
import com.credit.bladi.model.vo.Money;

public record InvestmentCreditResponse(
        String companyName,
        EnterpriseType enterpriseType,
        String sector,
        Money amount,
        Integer duration,
        InvestmentFinancialSnapshot snapshot,
        String demandReference
) {
}
