package com.credit.bladi.service.investment;

import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.investment.InvestmentCreditParametersModel;
import java.util.UUID;

public interface IInvestmentCreditParametersService {
    InvestmentCreditParametersModel getById(Long id);

    Long resolveParametersId(UUID publicId);

    InvestmentCreditParametersModel getActiveByEnterpriseType(EnterpriseType enterpriseType);

    InvestmentCreditParametersModel patchParameters(InvestmentCreditParametersModel patch);
}
