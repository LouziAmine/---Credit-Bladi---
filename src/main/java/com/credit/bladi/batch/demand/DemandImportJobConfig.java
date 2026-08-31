package com.credit.bladi.batch.demand;

import com.credit.bladi.batch.config.GenericChunkStepFactory;
import com.credit.bladi.batch.excel.ExcelRowItemReader;
import com.credit.bladi.service.importdemand.IDemandImportService;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import java.nio.file.Path;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class DemandImportJobConfig {
    private static final int CHUNK_SIZE = 20;
    public static final String JOB_NAME = "demandImportJob";

    @Bean
    @StepScope
    public ExcelRowItemReader<DemandExcelRow> demandImportReader(
            @Value("#{jobParameters['filePath']}") String filePath, MessageSource messageSource) {
        return new ExcelRowItemReader<>(Path.of(filePath), new DemandRowMapper(messageSource));
    }

    @Bean
    public DemandSnapshotProcessor demandImportProcessor() {
        return new DemandSnapshotProcessor();
    }

    @Bean
    public DemandImportItemWriter demandImportWriter(IWorkflowEngineService workflowEngineService) {
        return new DemandImportItemWriter(workflowEngineService);
    }

    @Bean
    @StepScope
    public DemandImportSkipListener demandImportSkipListener(IDemandImportService demandImportService,
            @Value("#{stepExecution}") StepExecution stepExecution) {
        return new DemandImportSkipListener(demandImportService, stepExecution.getJobExecutionId());
    }

    @Bean
    public Step demandImportStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
            ExcelRowItemReader<DemandExcelRow> demandImportReader, DemandSnapshotProcessor demandImportProcessor,
            DemandImportItemWriter demandImportWriter, DemandImportSkipListener demandImportSkipListener,
            @Value("${app.demand-import.max-skip-count:50}") int maxSkipCount) {
        return GenericChunkStepFactory.chunkStep("demandImportStep", jobRepository, transactionManager, CHUNK_SIZE,
            demandImportReader, demandImportProcessor, demandImportWriter, demandImportSkipListener, maxSkipCount);
    }

    @Bean(JOB_NAME)
    public Job demandImportJob(JobRepository jobRepository, Step demandImportStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
            .listener(new DemandImportFileCleanupListener())
            .start(demandImportStep)
            .build();
    }
}
