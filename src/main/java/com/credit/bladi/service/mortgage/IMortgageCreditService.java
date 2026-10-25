package com.credit.bladi.service.mortgage;

import com.credit.bladi.model.mortgage.MortgageAmortizationSnapshot;
import com.credit.bladi.model.mortgage.request.MortgageCreditRequest;
import com.credit.bladi.model.mortgage.response.MortgageCreditResponse;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;

public interface IMortgageCreditService {
    MortgageCreditResponse simulate(MortgageCreditRequest request);

    MortgageAmortizationSnapshot getAmortizationSchedule(Long demandId, Money amountOverride, Rate rateOverride,
            Integer durationOverride);
}
