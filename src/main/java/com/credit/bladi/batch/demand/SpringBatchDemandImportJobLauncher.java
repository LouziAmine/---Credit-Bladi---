package com.credit.bladi.batch.demand;

import com.credit.bladi.batch.config.ImportJobRegistry;
import com.credit.bladi.model.importdemand.DemandImportProgress;
import com.credit.bladi.service.importdemand.IDemandImportJobLauncher;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.batch.core.launch.support.TaskExecutorJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SpringBatchDemandImportJobLauncher implements IDemandImportJobLauncher {
    private final TaskExecutorJobOperator jobOperator;

    private final JobRepository jobRepository;

    private final ImportJobRegistry importJobRegistry;

    public SpringBatchDemandImportJobLauncher(@Qualifier("demandImportJobOperator") TaskExecutorJobOperator jobOperator,
                                              JobRepository jobRepository, ImportJobRegistry importJobRegistry) {
        this.jobOperator = jobOperator;
        this.jobRepository = jobRepository;
        this.importJobRegistry = importJobRegistry;
    }

    @Override
    public boolean isRunning() {
        return importJobRegistry.isRunning(DemandImportJobConfig.JOB_NAME);
    }

    @Override
    public Long launch(Path file, Instant requestedAt) {
        JobParameters parameters = new JobParametersBuilder()
            .addString("filePath", file.toAbsolutePath().toString())
            .addLong("timestamp", requestedAt.toEpochMilli())
            .toJobParameters();
        try {
            return jobOperator.start(importJobRegistry.getJob(DemandImportJobConfig.JOB_NAME), parameters).getId();
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
                 | InvalidJobParametersException e) {
            throw new IllegalStateException("Failed to start " + DemandImportJobConfig.JOB_NAME, e);
        }
    }

    @Override
    public Optional<DemandImportProgress> findProgress(Long jobExecutionId) {
        return Optional.ofNullable(jobRepository.getJobExecution(jobExecutionId))
            .map(SpringBatchDemandImportJobLauncher::toProgress);
    }

    private static DemandImportProgress toProgress(JobExecution execution) {
        long readCount = 0;
        long writeCount = 0;
        long skipCount = 0;
        for (StepExecution step : execution.getStepExecutions()) {
            readCount += step.getReadCount() + step.getReadSkipCount();
            writeCount += step.getWriteCount();
            skipCount += step.getSkipCount();
        }
        return new DemandImportProgress(execution.getStatus().name(), readCount, writeCount, skipCount,
            execution.getStartTime(), execution.getEndTime());
    }
}
