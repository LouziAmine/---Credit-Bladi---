package com.credit.bladi.model.importdemand;

import java.time.LocalDateTime;

public record DemandImportProgress(String status, long readCount, long writeCount, long skipCount,
                                   LocalDateTime startTime, LocalDateTime endTime) {
}
