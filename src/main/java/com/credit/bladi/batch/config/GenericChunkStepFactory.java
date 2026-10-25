package com.credit.bladi.batch.config;

import com.credit.bladi.batch.excel.ImportRowException;
import com.credit.bladi.batch.excel.ImportSkipPolicy;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.ChunkOrientedStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemStream;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

public final class GenericChunkStepFactory {
    private GenericChunkStepFactory() {
    }

    public static <I, O> Step chunkStep(String stepName, JobRepository jobRepository,
            PlatformTransactionManager transactionManager, int chunkSize,
            ItemReader<I> reader, ItemProcessor<I, O> processor, ItemWriter<O> writer,
            SkipListener<I, O> skipListener, int maxSkipCount) {
        ChunkOrientedStepBuilder<I, O> builder = new StepBuilder(stepName, jobRepository)
            .<I, O>chunk(chunkSize)
            .transactionManager(transactionManager)
            .transactionAttribute(new DefaultTransactionAttribute() {
                @Override
                public boolean rollbackOn(Throwable ex) {
                    return !(ex instanceof ImportRowException) && super.rollbackOn(ex);
                }
            })
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .faultTolerant()
            .skipPolicy(new ImportSkipPolicy(maxSkipCount))
            .skipListener(skipListener);

        if (reader instanceof ItemStream stream) {
            builder = builder.stream(stream);
        }
        if (writer instanceof ItemStream stream) {
            builder = builder.stream(stream);
        }
        return builder.build();
    }
}
