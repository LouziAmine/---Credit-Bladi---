package com.credit.bladi.batch.config;

import java.util.Map;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Component;

@Component
public class ImportJobRegistry {
    private final Map<String, Job> jobsByName;

    private final JobRepository jobRepository;

    public ImportJobRegistry(Map<String, Job> jobsByName, JobRepository jobRepository) {
        this.jobsByName = jobsByName;
        this.jobRepository = jobRepository;
    }

    public Job getJob(String jobName) {
        Job job = jobsByName.get(jobName);
        if (job == null) {
            throw new IllegalStateException("No Spring Batch job registered under name '" + jobName + "'");
        }
        return job;
    }

    public boolean isRunning(String jobName) {
        return !jobRepository.findRunningJobExecutions(jobName).isEmpty();
    }
}
