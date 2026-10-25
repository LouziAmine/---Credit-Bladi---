package com.credit.bladi.model.creditrate;

import com.credit.bladi.model.parameters.VersionedPatchResult;

public record CreditRatePatchResult(CreditRateModel before, CreditRateModel after)
    implements VersionedPatchResult<CreditRateModel> {
}
