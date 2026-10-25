package com.credit.bladi.repository.investment;

import static com.credit.bladi.exception.InvestmentCreditException.InvestmentCreditExceptionType.PARAMETERS_NOT_FOUND;

import com.credit.bladi.datasource.investment.InvestmentCreditParametersDatasource;
import com.credit.bladi.datasource.investment.entity.InvestmentCreditParametersEntity;
import com.credit.bladi.datasource.investment.mapper.InvestmentCreditParametersEntityMapper;
import com.credit.bladi.exception.InvestmentCreditException;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.investment.InvestmentCreditParametersModel;
import com.credit.bladi.model.investment.InvestmentCreditParametersPatchResult;
import java.util.Optional;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public class InvestmentCreditParametersRepository implements IInvestmentCreditParametersRepository {
    private static final Logger LOGGER = LogManager.getLogger(InvestmentCreditParametersRepository.class);

    private final InvestmentCreditParametersDatasource datasource;

    public InvestmentCreditParametersRepository(InvestmentCreditParametersDatasource datasource) {
        this.datasource = datasource;
    }

    @Override
    @Cacheable(value = "investmentCreditParameters", key = "#enterpriseType.name()", unless = "#result == null")
    public Optional<InvestmentCreditParametersModel> findActiveByEnterpriseType(EnterpriseType enterpriseType) {
        LOGGER.debug("findActiveByEnterpriseType - enterpriseType: {}", enterpriseType);
        Optional<InvestmentCreditParametersModel> result = datasource
            .findActiveByEnterpriseType(enterpriseType.name())
            .map(InvestmentCreditParametersEntityMapper::mapFromEntity);
        result.ifPresentOrElse(
            params -> LOGGER.debug("findActiveByEnterpriseType - result: {}", params),
            () -> LOGGER.warn("findActiveByEnterpriseType - no parameters found for enterpriseType: {}",
                enterpriseType));
        return result;
    }

    @Override
    public Optional<InvestmentCreditParametersModel> findById(Long id) {
        LOGGER.debug("findById - id: {}", id);
        return datasource.findById(id).map(InvestmentCreditParametersEntityMapper::mapFromEntity);
    }

    @Override
    public Optional<Long> findIdByPublicId(UUID publicId) {
        return datasource.findByPublicId(publicId).map(InvestmentCreditParametersEntity::getId);
    }

    @Override
    @CacheEvict(value = "investmentCreditParameters", key = "#result.after().enterpriseType.name()")
    public InvestmentCreditParametersPatchResult patchParameters(InvestmentCreditParametersModel patch) {
        InvestmentCreditParametersEntity entity = datasource.findById(patch.getId())
            .orElseThrow(() -> InvestmentCreditException.of(PARAMETERS_NOT_FOUND));
        InvestmentCreditParametersModel before = InvestmentCreditParametersEntityMapper.mapFromEntity(entity);
        entity.merge(InvestmentCreditParametersEntityMapper.mapToEntity(patch));
        if (Boolean.TRUE.equals(entity.getActive())) {
            datasource.deactivateOthersForEnterpriseType(entity.getEnterpriseType(), entity.getId());
        }
        datasource.save(entity);
        InvestmentCreditParametersModel merged = InvestmentCreditParametersEntityMapper.mapFromEntity(entity);
        LOGGER.debug("patchParameters - id: {}", patch.getId());
        return new InvestmentCreditParametersPatchResult(before, merged);
    }
}
