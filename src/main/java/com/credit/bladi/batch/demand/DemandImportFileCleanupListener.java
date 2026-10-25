package com.credit.bladi.batch.demand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;

public class DemandImportFileCleanupListener implements JobExecutionListener {
    private static final Logger LOGGER = LogManager.getLogger(DemandImportFileCleanupListener.class);

    @Override
    public void afterJob(JobExecution jobExecution) {
        String filePath = jobExecution.getJobParameters().getString("filePath");
        if (filePath == null) {
            return;
        }
        try {
            Files.deleteIfExists(Path.of(filePath));
        } catch (IOException e) {
            LOGGER.error("DEMAND_IMPORT_FILE_CLEANUP_FAILED jobExecutionId={}", jobExecution.getId(), e);
        }
    }
}
