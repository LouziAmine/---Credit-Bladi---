package com.credit.bladi.repository.bam;

import static com.credit.bladi.exception.BamParametersException.BamParametersExceptionType.NOT_FOUND;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.bam.BamParametersDatasource;
import com.credit.bladi.datasource.bam.entity.BamParametersEntity;
import com.credit.bladi.datasource.bam.mapper.BamParametersEntityMapper;
import com.credit.bladi.exception.BamParametersException;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.bam.BamParametersPatchResult;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class BamParametersRepository implements IBamParametersRepository {

    private static final Logger LOGGER = LogManager.getLogger(BamParametersRepository.class);

    private final BamParametersDatasource datasource;

    public BamParametersRepository(BamParametersDatasource datasource) {
        this.datasource = datasource;
    }

    @Override
    public CreditPage<BamParametersModel> findAll(int page, int size) {
        LOGGER.debug("findAll - page: {}, size: {}", page, size);
        Page<BamParametersEntity> result = datasource.findAll(PageRequest.of(page - 1, size, Sort.by("id")));
        return BamParametersEntityMapper.mapToPage(result.getContent(), result.getTotalElements(), page, size);
    }

    @Override
    public Optional<BamParametersModel> findById(Long id) {
        LOGGER.debug("findById - id: {}", id);
        return datasource.findById(id).map(BamParametersEntityMapper::mapFromEntity);
    }

    @Override
    @Cacheable(value = "bamParameters", unless = "#result == null")
    public Optional<BamParametersModel> getActiveParameters() {
        LOGGER.debug("getActiveParameters");
        long activeCount = datasource.countByActiveTrue();
        if (activeCount > 1) {
            LOGGER.warn("getActiveParameters - {} active bam_parameters rows found, expected at most 1 "
                + "— using the most recent one", activeCount);
        }
        Optional<BamParametersModel> result = datasource.findActiveParameters()
                .map(BamParametersEntityMapper::mapFromEntity);
        result.ifPresentOrElse(
                p -> LOGGER.debug("getActiveParameters - result: {}", p),
                ()  -> LOGGER.warn("getActiveParameters - no active parameters found")
        );
        return result;
    }

    @Override
    @Transactional
    @CacheEvict(value = "bamParameters", allEntries = true)
    public BamParametersPatchResult patchParameters(BamParametersModel bamParametersPatch) {
        BamParametersEntity entity = datasource.findById(bamParametersPatch.getId())
            .orElseThrow(() -> BamParametersException.of(NOT_FOUND));
        BamParametersModel before = BamParametersEntityMapper.mapFromEntity(entity);
        entity.merge(BamParametersEntityMapper.mapToEntity(bamParametersPatch));
        BamParametersModel merged = BamParametersEntityMapper.mapFromEntity(entity);
        if (Boolean.TRUE.equals(entity.getActive())) {
            datasource.deactivateAllExcept(entity.getId());
        }
        datasource.save(entity);
        LOGGER.debug("patchParameters - id: {}", bamParametersPatch.getId());
        return new BamParametersPatchResult(before, merged);
    }
}
