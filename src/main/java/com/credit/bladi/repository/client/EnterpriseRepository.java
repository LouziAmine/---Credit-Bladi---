package com.credit.bladi.repository.client;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.client.EnterpriseDatasource;
import com.credit.bladi.datasource.client.entity.EnterpriseEntity;
import com.credit.bladi.datasource.client.mapper.EnterpriseEntityMapper;
import com.credit.bladi.datasource.client.mapper.EnterpriseNameNormalizer;
import com.credit.bladi.model.client.EnterpriseModel;
import com.credit.bladi.model.vo.Rib;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class EnterpriseRepository implements IEnterpriseRepository {
    private final EnterpriseDatasource datasource;

    private final Clock clock;

    public EnterpriseRepository(EnterpriseDatasource datasource, Clock clock) {
        this.clock = clock;
        this.datasource = datasource;
    }

    @Override
    public EnterpriseModel create(String name, String sector, String registrationNumber) {
        EnterpriseEntity entity = EnterpriseEntityMapper.mapToNewEntity(name, sector, registrationNumber);
        entity.setCreatedAt(clock.instant());
        datasource.save(entity);
        return EnterpriseEntityMapper.mapFromEntity(entity);
    }

    @Override
    public CreditPage<EnterpriseModel> findAll(int page, int size) {
        Page<EnterpriseEntity> result = datasource.findAll(PageRequest.of(page - 1, size, Sort.by("name")));
        return CreditPage.of(result.getContent().stream().map(EnterpriseEntityMapper::mapFromEntity).toList(),
            page, size, result.getTotalElements());
    }

    @Override
    public Optional<EnterpriseModel> findById(Long id) {
        return datasource.findById(id).map(EnterpriseEntityMapper::mapFromEntity);
    }

    @Override
    public List<EnterpriseModel> findAllByIds(List<Long> ids) {
        return datasource.findAllById(ids).stream().map(EnterpriseEntityMapper::mapFromEntity).toList();
    }

    @Override
    public Optional<Long> findIdByPublicId(UUID publicId) {
        return datasource.findByPublicId(publicId).map(EnterpriseEntity::getId);
    }

    @Override
    public boolean existsById(Long id) {
        return datasource.existsById(id);
    }

    @Override
    public void updateRib(Long id, Rib rib) {
        datasource.updateRib(id, rib.value());
    }

    @Override
    public Optional<EnterpriseModel> findMatchByName(String rawName) {
        String normalizedName = EnterpriseNameNormalizer.normalize(rawName);
        if (normalizedName.isEmpty()) {
            return Optional.empty();
        }
        return datasource.findByNormalizedNameOrderByCreatedAtAsc(normalizedName).stream()
            .findFirst()
            .map(EnterpriseEntityMapper::mapFromEntity);
    }

    @Override
    public Optional<EnterpriseModel> findByRegistrationNumber(String registrationNumber) {
        return datasource.findByRegistrationNumber(registrationNumber).map(EnterpriseEntityMapper::mapFromEntity);
    }
}
