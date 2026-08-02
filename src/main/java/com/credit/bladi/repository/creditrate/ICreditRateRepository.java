package com.credit.bladi.repository.creditrate;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.simulation.CreditRateModel;
import com.credit.bladi.model.simulation.CreditRatePatchResult;
import java.util.Optional;

public interface ICreditRateRepository {

    Optional<CreditRateModel> findActiveByCspCode(CspCode cspCode);

    CreditPage<CreditRateModel> findAll(int page, int size);

    Optional<CreditRateModel> findById(Long id);

    CreditRatePatchResult patchParameters(CreditRateModel creditRatePatch);
}
