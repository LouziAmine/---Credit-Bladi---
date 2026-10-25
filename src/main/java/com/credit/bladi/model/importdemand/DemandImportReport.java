package com.credit.bladi.model.importdemand;

import java.time.LocalDateTime;
import java.util.List;

public record DemandImportReport(Long jobExecutionId, String status, long readCount, long writeCount,
                                  long skipCount, LocalDateTime startTime, LocalDateTime endTime,
                                  List<DemandImportRowErrorModel> rowErrors) {
}
