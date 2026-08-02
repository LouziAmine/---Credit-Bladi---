package com.credit.bladi.repository.mourabaha;

import static com.credit.bladi.exception.MourabahaParametersException.MourabahaParametersExceptionType.NOT_FOUND;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.mourabaha.MourabahaParametersDatasource;
import com.credit.bladi.datasource.mourabaha.entity.MourabahaParametersEntity;
import com.credit.bladi.datasource.mourabaha.mapper.MourabahaParametersEntityMapper;
import com.credit.bladi.exception.MourabahaParametersException;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import com.credit.bladi.model.mourabaha.MourabahaParametersPatchResult;
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
public class MourabahaParametersRepository implements IMourabahaParametersRepository {

    private static final Logger LOGGER = LogManager.getLogger(MourabahaParametersRepository.class);

    private final MourabahaParametersDatasource datasource;

    public MourabahaParametersRepository(MourabahaParametersDatasource datasource) {
        this.datasource = datasource;
    }

    @Override
    public CreditPage<MourabahaParametersModel> findAll(int page, int size) {
        LOGGER.debug("findAll - page: {}, size: {}", page, size);
        Page<MourabahaParametersEntity> result = datasource.findAll(PageRequest.of(page - 1, size, Sort.by("id")));
        return MourabahaParametersEntityMapper.mapToPage(result.getContent(), result.getTotalElements(), page, size);
    }

    @Override
    public Optional<MourabahaParametersModel> findById(Long id) {
        LOGGER.debug("findById - id: {}", id);
        return datasource.findById(id).map(MourabahaParametersEntityMapper::mapFromEntity);
    }

    @Override
    @Cacheable(value = "mourabahaParameters", unless = "#result == null")
    public Optional<MourabahaParametersModel> getActiveParameters() {
        LOGGER.debug("getActiveParameters");
        long activeCount = datasource.countByActiveTrue();
        if (activeCount > 1) {
            LOGGER.warn("getActiveParameters - {} active mourabaha_parameters rows found, expected at most 1 "
                + "— using the most recent one", activeCount);
        }
        Optional<MourabahaParametersModel> result = datasource.findActiveParameters()
                .map(MourabahaParametersEntityMapper::mapFromEntity);
        result.ifPresentOrElse(
                p -> LOGGER.debug("getActiveParameters - result: {}", p),
                () -> LOGGER.warn("getActiveParameters - no active parameters found"));
        return result;
    }

    @Override
    @Transactional
    @CacheEvict(value = "mourabahaParameters", allEntries = true)
    public MourabahaParametersPatchResult patchParameters(MourabahaParametersModel mourabahaParametersPatch) {
        MourabahaParametersEntity entity = datasource.findById(mourabahaParametersPatch.getId())
            .orElseThrow(() -> MourabahaParametersException.of(NOT_FOUND));
        MourabahaParametersModel before = MourabahaParametersEntityMapper.mapFromEntity(entity);
        entity.merge(MourabahaParametersEntityMapper.mapToEntity(mourabahaParametersPatch));
        MourabahaParametersModel merged = MourabahaParametersEntityMapper.mapFromEntity(entity);
        if (Boolean.TRUE.equals(entity.getActive())) {
            datasource.deactivateAllExcept(entity.getId());
        }
        datasource.save(entity);
        LOGGER.debug("patchParameters - id: {}", mourabahaParametersPatch.getId());
        return new MourabahaParametersPatchResult(before, merged);
    }
}
