package com.credit.bladi.repository.workflow.detail;

import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.WORKFLOW_NOT_CONFIGURED;

import com.credit.bladi.exception.WorkflowException;
import com.credit.bladi.model.enums.DemandType;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class DemandDetailStoreFactory {
    private final Map<DemandType, DemandDetailStore> storesByType;

    public DemandDetailStoreFactory(List<DemandDetailStore> stores) {
        this.storesByType = stores.stream()
            .collect(Collectors.toMap(DemandDetailStore::supportedType, Function.identity()));
    }

    public DemandDetailStore getStore(DemandType demandType) {
        DemandDetailStore store = storesByType.get(demandType);
        if (store == null) {
            throw WorkflowException.of(WORKFLOW_NOT_CONFIGURED);
        }
        return store;
    }
}
