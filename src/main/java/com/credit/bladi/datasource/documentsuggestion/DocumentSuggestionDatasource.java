package com.credit.bladi.datasource.documentsuggestion;

import com.credit.bladi.datasource.documentsuggestion.entity.DocumentSuggestionEntity;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.EnterpriseType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentSuggestionDatasource extends JpaRepository<DocumentSuggestionEntity, Long> {
    Optional<DocumentSuggestionEntity> findByPublicId(UUID publicId);

    @Query("SELECT e FROM DocumentSuggestionEntity e "
        + "WHERE e.clientScope = com.credit.bladi.model.enums.DocumentSuggestionScope.ENTERPRISE "
        + "AND (e.enterpriseType = :type OR e.enterpriseType IS NULL) ORDER BY e.label ASC")
    List<DocumentSuggestionEntity> findApplicableTo(@Param("type") EnterpriseType type);

    @Query("SELECT e FROM DocumentSuggestionEntity e "
        + "WHERE e.clientScope = com.credit.bladi.model.enums.DocumentSuggestionScope.CSP "
        + "AND (e.cspCode = :cspCode OR e.cspCode IS NULL) ORDER BY e.label ASC")
    List<DocumentSuggestionEntity> findApplicableToCsp(@Param("cspCode") CspCode cspCode);

    List<DocumentSuggestionEntity> findAll(Sort sort);

    boolean existsByEnterpriseTypeAndLabelIgnoreCase(EnterpriseType enterpriseType, String label);

    boolean existsByCspCodeAndLabelIgnoreCase(CspCode cspCode, String label);
}
