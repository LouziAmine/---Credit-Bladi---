package com.credit.bladi.repository.investment;

import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.investment.InvestmentCreditParametersModel;
import com.credit.bladi.model.investment.InvestmentCreditParametersPatchResult;
import java.util.Optional;
import java.util.UUID;

public interface IInvestmentCreditParametersRepository {
    Optional<InvestmentCreditParametersModel> findActiveByEnterpriseType(EnterpriseType enterpriseType);

    Optional<InvestmentCreditParametersModel> findById(Long id);

    Optional<Long> findIdByPublicId(UUID publicId);

    InvestmentCreditParametersPatchResult patchParameters(InvestmentCreditParametersModel patch);
}
