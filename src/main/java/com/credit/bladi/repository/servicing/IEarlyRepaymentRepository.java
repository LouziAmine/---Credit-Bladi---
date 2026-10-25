package com.credit.bladi.repository.servicing;

import com.credit.bladi.model.servicing.EarlyRepaymentModel;
import com.credit.bladi.model.servicing.EarlyRepaymentQuote;
import java.time.Instant;
import java.util.List;

public interface IEarlyRepaymentRepository {
    EarlyRepaymentModel create(Long creditFileId, EarlyRepaymentQuote quote, String performedBy, Instant performedAt);

    List<EarlyRepaymentModel> findByCreditFileId(Long creditFileId);
}
