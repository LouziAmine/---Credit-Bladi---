package com.credit.bladi.unittest.batch;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_COMPLETED;
import static com.credit.bladi.unittest.mock.data.MockDates.DATE_2026_10_11T10_00_00Z;
import static com.credit.bladi.unittest.mock.data.MockFiles.FILE_DEMANDS_XLSX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.batch.config.ImportJobRegistry;
import com.credit.bladi.batch.demand.DemandImportJobConfig;
import com.credit.bladi.batch.demand.SpringBatchDemandImportJobLauncher;
import com.credit.bladi.model.importdemand.DemandImportProgress;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.JobInstance;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.batch.core.launch.support.TaskExecutorJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.StepExecution;

@ExtendWith(MockitoExtension.class)
class SpringBatchDemandImportJobLauncherTest {
    @Mock
    private TaskExecutorJobOperator jobOperator;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private ImportJobRegistry importJobRegistry;

    @Mock
    private Job demandImportJob;

    private SpringBatchDemandImportJobLauncher launcher;

    @BeforeEach
    void setUp() {
        launcher = new SpringBatchDemandImportJobLauncher(jobOperator, jobRepository, importJobRegistry);
    }

    private static JobExecution newJobExecution(long id) {
        return new JobExecution(id, new JobInstance(1L, DemandImportJobConfig.JOB_NAME), new JobParameters());
    }

    @Test
    void shouldReportWhetherTheImportJobIsRunning() {
        when(importJobRegistry.isRunning(DemandImportJobConfig.JOB_NAME)).thenReturn(true);

        assertTrue(launcher.isRunning());
    }

    @Test
    void shouldStartTheImportJobWithTheFilePathAndTimestamp() throws Exception {
        when(importJobRegistry.getJob(DemandImportJobConfig.JOB_NAME)).thenReturn(demandImportJob);
        when(jobOperator.start(eq(demandImportJob), any(JobParameters.class))).thenReturn(newJobExecution(100L));
        Path file = Path.of("/tmp/imports/demands.xlsx");
        Instant requestedAt = Instant.parse(DATE_2026_10_11T10_00_00Z);

        Long jobExecutionId = launcher.launch(file, requestedAt);

        assertEquals(100L, jobExecutionId);
        ArgumentCaptor<JobParameters> parametersCaptor = ArgumentCaptor.forClass(JobParameters.class);
        verify(jobOperator).start(eq(demandImportJob), parametersCaptor.capture());
        assertEquals(file.toAbsolutePath().toString(), parametersCaptor.getValue().getString("filePath"));
        assertEquals(requestedAt.toEpochMilli(), parametersCaptor.getValue().getLong("timestamp"));
    }

    @Test
    void shouldWrapJobStartFailureAsIllegalStateException() throws Exception {
        when(importJobRegistry.getJob(DemandImportJobConfig.JOB_NAME)).thenReturn(demandImportJob);
        when(jobOperator.start(eq(demandImportJob), any(JobParameters.class)))
            .thenThrow(new JobRestartException("boom"));

        assertThrows(IllegalStateException.class, () -> launcher.launch(Path.of(FILE_DEMANDS_XLSX), Instant.now()));
    }

    @Test
    void shouldCountRowsRejectedWhileReadingAsReadRows() {
        JobExecution execution = newJobExecution(42L);
        execution.setStatus(BatchStatus.COMPLETED);
        LocalDateTime start = LocalDateTime.now().minusMinutes(5);
        LocalDateTime end = LocalDateTime.now();
        execution.setStartTime(start);
        execution.setEndTime(end);
        StepExecution step = new StepExecution("demandImportStep", execution);
        step.setReadCount(47);
        step.setWriteCount(47);
        step.setReadSkipCount(3);
        execution.addStepExecution(step);
        when(jobRepository.getJobExecution(42L)).thenReturn(execution);

        DemandImportProgress progress = launcher.findProgress(42L).orElseThrow();

        assertEquals(new DemandImportProgress(CODE_COMPLETED, 50L, 47L, 3L, start, end), progress);
    }

    @Test
    void shouldReturnNothingForAnUnknownJobExecution() {
        when(jobRepository.getJobExecution(99L)).thenReturn(null);

        assertTrue(launcher.findProgress(99L).isEmpty());
    }
}
