package com.credit.bladi.batch.excel;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

public class ImportSkipPolicy implements SkipPolicy {
    private final int maxSkipCount;

    public ImportSkipPolicy(int maxSkipCount) {
        this.maxSkipCount = maxSkipCount;
    }

    @Override
    public boolean shouldSkip(Throwable throwable, long skipCount) throws SkipLimitExceededException {
        while (throwable != null) {
            if (throwable instanceof ImportRowException) {
                return skipCount < maxSkipCount;
            }
            throwable = throwable.getCause();
        }
        return false;
    }
}
