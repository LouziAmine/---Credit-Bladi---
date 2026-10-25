package com.credit.bladi.service.creditrate;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.creditrate.CreditRateModel;
import java.util.UUID;

public interface ICreditRateService {
    CreditPage<CreditRateModel> getAllParameters(int page, int size);

    CreditRateModel getById(Long id);

    Long resolveCreditRateId(UUID publicId);

    CreditRateModel patchParameters(CreditRateModel creditRatePatch);
}
