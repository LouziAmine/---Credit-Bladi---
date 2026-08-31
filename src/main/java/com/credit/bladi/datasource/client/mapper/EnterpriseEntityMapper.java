package com.credit.bladi.datasource.client.mapper;

import static com.credit.bladi.model.vo.ValueObjectMappers.toRib;

import com.credit.bladi.datasource.client.entity.EnterpriseEntity;
import com.credit.bladi.model.client.EnterpriseModel;

public final class EnterpriseEntityMapper {
    private EnterpriseEntityMapper() {
    }

    public static EnterpriseModel mapFromEntity(EnterpriseEntity entity) {
        return new EnterpriseModel(entity.getId(), entity.getPublicId(), entity.getName(), entity.getSector(),
            entity.getRegistrationNumber(), toRib(entity.getRib()), entity.getCreatedAt());
    }

    public static EnterpriseEntity mapToNewEntity(String name, String sector, String registrationNumber) {
        EnterpriseEntity entity = new EnterpriseEntity();
        entity.setName(name);
        entity.setSector(sector);
        entity.setNormalizedName(EnterpriseNameNormalizer.normalize(name));
        entity.setRegistrationNumber(registrationNumber);
        return entity;
    }
}
