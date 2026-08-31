package com.credit.bladi.repository.borrower;

import com.credit.bladi.model.borrower.MortgageBorrowerModel;
import com.credit.bladi.model.enums.BorrowerLinkOrigin;
import java.util.List;
import java.util.Optional;

public interface IMortgageBorrowerRepository {
    MortgageBorrowerModel link(Long demandId, Long userId, BorrowerLinkOrigin origin, String linkedBy);

    Optional<MortgageBorrowerModel> findByDemandId(Long demandId);

    boolean isBorrower(Long demandId, Long userId);

    List<Long> findDemandIdsByUserId(Long userId);
}
