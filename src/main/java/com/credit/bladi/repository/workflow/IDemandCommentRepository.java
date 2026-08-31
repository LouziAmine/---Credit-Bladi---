package com.credit.bladi.repository.workflow;

import com.credit.bladi.model.workflow.DemandCommentModel;
import java.util.List;

public interface IDemandCommentRepository {
    DemandCommentModel add(Long demandId, String stepCode, String stepName, String username, String comment);

    List<DemandCommentModel> findByDemandId(Long demandId);
}
