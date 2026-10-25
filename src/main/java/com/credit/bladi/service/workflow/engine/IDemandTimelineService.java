package com.credit.bladi.service.workflow.engine;

import com.credit.bladi.model.workflow.DemandAssignmentHistoryModel;
import com.credit.bladi.model.workflow.DemandCommentModel;
import com.credit.bladi.model.workflow.DemandHistoryEntryModel;
import com.credit.bladi.model.workflow.DemandPipelineStepModel;
import java.util.List;

public interface IDemandTimelineService {
    List<DemandHistoryEntryModel> getHistory(Long demandId);

    List<DemandPipelineStepModel> getPipeline(Long demandId);

    List<DemandAssignmentHistoryModel> getAssignmentHistory(Long demandId);

    DemandCommentModel addComment(Long demandId, String comment);

    List<DemandCommentModel> getComments(Long demandId);
}
