package com.credit.bladi.repository.documentsuggestion;

import com.credit.bladi.datasource.documentsuggestion.DocumentSuggestionDatasource;
import com.credit.bladi.datasource.documentsuggestion.entity.DocumentSuggestionEntity;
import com.credit.bladi.datasource.documentsuggestion.mapper.DocumentSuggestionEntityMapper;
import com.credit.bladi.model.documentsuggestion.DocumentSuggestionModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.DocumentSuggestionScope;
import com.credit.bladi.model.enums.EnterpriseType;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentSuggestionRepository implements IDocumentSuggestionRepository {
    private final DocumentSuggestionDatasource datasource;

    private final Clock clock;

    public DocumentSuggestionRepository(DocumentSuggestionDatasource datasource, Clock clock) {
        this.clock = clock;
        this.datasource = datasource;
    }

    @Override
    public DocumentSuggestionModel create(DocumentSuggestionScope scope, EnterpriseType enterpriseType,
            CspCode cspCode, String label) {
        DocumentSuggestionEntity entity = DocumentSuggestionEntityMapper.mapToNewEntity(scope, enterpriseType,
            cspCode, label);
        entity.setCreatedAt(clock.instant());
        datasource.save(entity);
        return DocumentSuggestionEntityMapper.mapFromEntity(entity);
    }

    @Override
    public Optional<Long> findIdByPublicId(UUID publicId) {
        return datasource.findByPublicId(publicId).map(DocumentSuggestionEntity::getId);
    }

    @Override
    public List<DocumentSuggestionModel> findApplicableTo(EnterpriseType enterpriseType) {
        return datasource.findApplicableTo(enterpriseType).stream()
            .map(DocumentSuggestionEntityMapper::mapFromEntity)
            .toList();
    }

    @Override
    public List<DocumentSuggestionModel> findApplicableToCsp(CspCode cspCode) {
        return datasource.findApplicableToCsp(cspCode).stream()
            .map(DocumentSuggestionEntityMapper::mapFromEntity)
            .toList();
    }

    @Override
    public List<DocumentSuggestionModel> findAll() {
        return datasource.findAll(Sort.by("label")).stream()
            .map(DocumentSuggestionEntityMapper::mapFromEntity)
            .toList();
    }

    @Override
    public boolean existsByEnterpriseTypeAndLabelIgnoreCase(EnterpriseType enterpriseType, String label) {
        return datasource.existsByEnterpriseTypeAndLabelIgnoreCase(enterpriseType, label);
    }

    @Override
    public boolean existsByCspCodeAndLabelIgnoreCase(CspCode cspCode, String label) {
        return datasource.existsByCspCodeAndLabelIgnoreCase(cspCode, label);
    }

    @Override
    public boolean existsById(Long id) {
        return datasource.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        datasource.deleteById(id);
    }
}
