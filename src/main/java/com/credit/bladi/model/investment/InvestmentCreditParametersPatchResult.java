package com.credit.bladi.model.investment;

import com.credit.bladi.model.parameters.VersionedPatchResult;

public record InvestmentCreditParametersPatchResult(
        InvestmentCreditParametersModel before, InvestmentCreditParametersModel after)
    implements VersionedPatchResult<InvestmentCreditParametersModel> {
}
