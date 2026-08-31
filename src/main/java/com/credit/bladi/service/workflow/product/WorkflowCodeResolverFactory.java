package com.credit.bladi.service.workflow.product;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class WorkflowCodeResolverFactory extends DemandTypeRegistry<WorkflowCodeResolver> {
    public WorkflowCodeResolverFactory(List<WorkflowCodeResolver> resolvers) {
        super(resolvers, "workflow code resolver");
    }
}
