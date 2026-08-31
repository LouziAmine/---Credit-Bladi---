package com.credit.bladi.repository.creditrate;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.creditrate.CreditRateModel;
import com.credit.bladi.model.creditrate.CreditRatePatchResult;
import com.credit.bladi.model.enums.CspCode;
import java.util.Optional;
import java.util.UUID;

public interface ICreditRateRepository {
    Optional<CreditRateModel> findActiveByCspCode(CspCode cspCode);

    CreditPage<CreditRateModel> findAll(int page, int size);

    Optional<CreditRateModel> findById(Long id);

    Optional<Long> findIdByPublicId(UUID publicId);

    CreditRatePatchResult patchParameters(CreditRateModel creditRatePatch);
}
