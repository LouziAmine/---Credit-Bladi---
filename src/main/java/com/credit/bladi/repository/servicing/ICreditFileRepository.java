package com.credit.bladi.repository.servicing;

import com.credit.bladi.model.servicing.CreditFileModel;
import com.credit.bladi.model.vo.Money;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ICreditFileRepository {
    CreditFileModel create(Long demandId, String reference, Instant disbursedAt, Money disbursedAmount,
            String managedByUsername);

    Optional<CreditFileModel> findById(Long id);

    List<CreditFileModel> findAllById(List<Long> ids);

    Optional<CreditFileModel> findByDemandId(Long demandId);

    Optional<CreditFileModel> findByDemandIdForUpdate(Long demandId);

    boolean closeIfActive(Long id);
}
