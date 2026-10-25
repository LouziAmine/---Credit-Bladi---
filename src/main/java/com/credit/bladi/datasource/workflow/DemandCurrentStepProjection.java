package com.credit.bladi.datasource.workflow;

public interface DemandCurrentStepProjection {
    Long getDemandId();

    String getProcessName();

    String getStepName();
}
