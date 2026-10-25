package com.credit.bladi.model.importdemand;

import java.time.Instant;

public record DemandImportRowErrorModel(Long id, Long jobExecutionId, Integer rowNumber, String companyName,
                                         String errorMessage, Instant createdAt) {
}
