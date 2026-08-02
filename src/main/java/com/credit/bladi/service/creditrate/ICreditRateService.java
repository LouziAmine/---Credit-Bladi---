package com.credit.bladi.service.creditrate;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.simulation.CreditRateModel;

public interface ICreditRateService {

    CreditPage<CreditRateModel> getAllParameters(int page, int size);

    CreditRateModel getById(Long id);

    CreditRateModel patchParameters(CreditRateModel creditRatePatch);
}
