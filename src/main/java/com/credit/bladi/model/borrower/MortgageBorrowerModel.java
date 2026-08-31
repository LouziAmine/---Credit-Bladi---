package com.credit.bladi.model.borrower;

import com.credit.bladi.model.enums.BorrowerLinkOrigin;
import java.time.Instant;

public record MortgageBorrowerModel(Long demandId, Long userId, BorrowerLinkOrigin origin, String linkedBy,
                                    Instant linkedAt) {
}
