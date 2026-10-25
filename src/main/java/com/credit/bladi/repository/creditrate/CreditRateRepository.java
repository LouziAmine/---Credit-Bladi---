package com.credit.bladi.repository.creditrate;

import static com.credit.bladi.exception.CreditRateException.CreditRateExceptionType.NOT_FOUND;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.creditrate.CreditRateDatasource;
import com.credit.bladi.datasource.creditrate.entity.CreditRateEntity;
import com.credit.bladi.datasource.creditrate.mapper.CreditRateEntityMapper;
import com.credit.bladi.exception.CreditRateException;
import com.credit.bladi.model.creditrate.CreditRateModel;
import com.credit.bladi.model.creditrate.CreditRatePatchResult;
import com.credit.bladi.model.enums.CspCode;
import java.util.Optional;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class CreditRateRepository implements ICreditRateRepository {
    private static final Logger LOGGER = LogManager.getLogger(CreditRateRepository.class);

    private final CreditRateDatasource datasource;

    public CreditRateRepository(CreditRateDatasource datasource) {
        this.datasource = datasource;
    }

    @Override
    @Cacheable(value = "creditRates", key = "#cspCode.name()", unless = "#result == null")
    public Optional<CreditRateModel> findActiveByCspCode(CspCode cspCode) {
        LOGGER.debug("findActiveByCspCode - cspCode: {}", cspCode);
        Optional<CreditRateModel> result = datasource.findActiveByCspCode(cspCode.name())
                .map(CreditRateEntityMapper::mapFromEntity);
        result.ifPresentOrElse(
                rate -> LOGGER.debug("findActiveByCspCode - result: {}", rate),
                ()   -> LOGGER.warn("findActiveByCspCode - no rate found for cspCode: {}", cspCode)
        );
        return result;
    }

    @Override
    public CreditPage<CreditRateModel> findAll(int page, int size) {
        LOGGER.debug("findAll - page: {}, size: {}", page, size);
        Page<CreditRateEntity> result = datasource.findAll(PageRequest.of(page - 1, size, Sort.by("id")));
        return CreditRateEntityMapper.mapToPage(result.getContent(), result.getTotalElements(), page, size);
    }

    @Override
    public Optional<CreditRateModel> findById(Long id) {
        LOGGER.debug("findById - id: {}", id);
        return datasource.findById(id).map(CreditRateEntityMapper::mapFromEntity);
    }

    @Override
    public Optional<Long> findIdByPublicId(UUID publicId) {
        return datasource.findByPublicId(publicId).map(CreditRateEntity::getId);
    }

    @Override
    @CacheEvict(value = "creditRates", key = "#result.after().cspCode.name()")
    public CreditRatePatchResult patchParameters(CreditRateModel creditRatePatch) {
        CreditRateEntity entity = datasource.findById(creditRatePatch.getId())
            .orElseThrow(() -> CreditRateException.of(NOT_FOUND));
        CreditRateModel before = CreditRateEntityMapper.mapFromEntity(entity);
        entity.merge(CreditRateEntityMapper.mapToEntity(creditRatePatch));
        if (Boolean.TRUE.equals(entity.getActive())) {
            datasource.deactivateOthersForCspCode(entity.getCspCode(), entity.getId());
        }
        datasource.save(entity);
        CreditRateModel merged = CreditRateEntityMapper.mapFromEntity(entity);
        LOGGER.debug("patchParameters - id: {}", creditRatePatch.getId());
        return new CreditRatePatchResult(before, merged);
    }
}
