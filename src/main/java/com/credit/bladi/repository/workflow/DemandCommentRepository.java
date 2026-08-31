package com.credit.bladi.repository.workflow;

import com.credit.bladi.datasource.workflow.DemandCommentDatasource;
import com.credit.bladi.datasource.workflow.entity.DemandCommentEntity;
import com.credit.bladi.model.workflow.DemandCommentModel;
import java.time.Clock;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class DemandCommentRepository implements IDemandCommentRepository {
    private final DemandCommentDatasource datasource;

    private final Clock clock;

    public DemandCommentRepository(DemandCommentDatasource datasource, Clock clock) {
        this.datasource = datasource;
        this.clock = clock;
    }

    @Override
    public DemandCommentModel add(Long demandId, String stepCode, String stepName, String username, String comment) {
        DemandCommentEntity entity = new DemandCommentEntity();
        entity.setDemandId(demandId);
        entity.setStepCode(stepCode);
        entity.setStepName(stepName);
        entity.setUsername(username);
        entity.setComment(comment);
        entity.setCreatedAt(clock.instant());
        datasource.save(entity);
        return toModel(entity);
    }

    @Override
    public List<DemandCommentModel> findByDemandId(Long demandId) {
        return datasource.findByDemandIdOrderByCreatedAtAsc(demandId).stream()
            .map(DemandCommentRepository::toModel)
            .toList();
    }

    private static DemandCommentModel toModel(DemandCommentEntity e) {
        return new DemandCommentModel(e.getId(), e.getPublicId(), e.getStepCode(), e.getStepName(), e.getUsername(),
            e.getComment(), e.getCreatedAt());
    }
}
