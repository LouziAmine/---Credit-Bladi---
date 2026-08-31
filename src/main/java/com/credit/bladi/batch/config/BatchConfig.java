package com.credit.bladi.batch.config;

import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.launch.support.TaskExecutorJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.VirtualThreadTaskExecutor;

@Configuration
public class BatchConfig {
    @Bean
    public TaskExecutorJobOperator demandImportJobOperator(JobRepository jobRepository,
            @Qualifier("demandImportTaskExecutor") AsyncTaskExecutor taskExecutor) throws Exception {
        TaskExecutorJobOperator jobOperator = new TaskExecutorJobOperator();
        jobOperator.setJobRepository(jobRepository);
        jobOperator.setTaskExecutor(taskExecutor);
        jobOperator.setJobRegistry(new MapJobRegistry());
        jobOperator.afterPropertiesSet();
        return jobOperator;
    }

    @Bean("demandImportTaskExecutor")
    public AsyncTaskExecutor demandImportTaskExecutor() {
        return new VirtualThreadTaskExecutor("demand-import-");
    }
}
