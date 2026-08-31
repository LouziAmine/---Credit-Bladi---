package com.credit.bladi.repository.borrower;

import com.credit.bladi.datasource.borrower.MortgageBorrowerDatasource;
import com.credit.bladi.datasource.borrower.entity.MortgageBorrowerEntity;
import com.credit.bladi.model.borrower.MortgageBorrowerModel;
import com.credit.bladi.model.enums.BorrowerLinkOrigin;
import com.credit.bladi.repository.DuplicateEntryException;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
public class MortgageBorrowerRepository implements IMortgageBorrowerRepository {
    private final MortgageBorrowerDatasource datasource;

    private final Clock clock;

    public MortgageBorrowerRepository(MortgageBorrowerDatasource datasource, Clock clock) {
        this.datasource = datasource;
        this.clock = clock;
    }

    @Override
    public MortgageBorrowerModel link(Long demandId, Long userId, BorrowerLinkOrigin origin, String linkedBy) {
        if (datasource.existsById(demandId)) {
            throw new DuplicateEntryException(new DataIntegrityViolationException(
                "Mortgage demand " + demandId + " already has a borrower"));
        }
        MortgageBorrowerEntity entity = new MortgageBorrowerEntity();
        entity.setDemandId(demandId);
        entity.setUserId(userId);
        entity.setOrigin(origin);
        entity.setLinkedBy(linkedBy);
        entity.setLinkedAt(clock.instant());
        try {
            datasource.saveAndFlush(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEntryException(e);
        }
        return toModel(entity);
    }

    @Override
    public Optional<MortgageBorrowerModel> findByDemandId(Long demandId) {
        return datasource.findById(demandId).map(MortgageBorrowerRepository::toModel);
    }

    @Override
    public boolean isBorrower(Long demandId, Long userId) {
        return datasource.existsByDemandIdAndUserId(demandId, userId);
    }

    @Override
    public List<Long> findDemandIdsByUserId(Long userId) {
        return datasource.findByUserIdOrderByLinkedAtDesc(userId).stream()
            .map(MortgageBorrowerEntity::getDemandId)
            .toList();
    }

    private static MortgageBorrowerModel toModel(MortgageBorrowerEntity entity) {
        return new MortgageBorrowerModel(entity.getDemandId(), entity.getUserId(), entity.getOrigin(),
            entity.getLinkedBy(), entity.getLinkedAt());
    }
}
