package com.credit.bladi.service.importdemand;

import com.credit.bladi.model.importdemand.DemandImportProgress;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;

public interface IDemandImportJobLauncher {
    boolean isRunning();

    Long launch(Path file, Instant requestedAt);

    Optional<DemandImportProgress> findProgress(Long jobExecutionId);
}
