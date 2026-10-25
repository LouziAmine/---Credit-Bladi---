package com.credit.bladi.repository.reference;

import com.credit.bladi.datasource.reference.GradeDatasource;
import com.credit.bladi.datasource.reference.TeamDatasource;
import com.credit.bladi.datasource.reference.entity.GradeEntity;
import com.credit.bladi.datasource.reference.entity.TeamEntity;
import com.credit.bladi.model.reference.Grade;
import com.credit.bladi.model.reference.Team;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public class ReferenceDataRepository implements IReferenceDataRepository {
    private final GradeDatasource gradeDatasource;

    private final TeamDatasource teamDatasource;

    public ReferenceDataRepository(GradeDatasource gradeDatasource, TeamDatasource teamDatasource) {
        this.gradeDatasource = gradeDatasource;
        this.teamDatasource = teamDatasource;
    }

    @Override
    @Cacheable("grades")
    public List<Grade> findAllGrades() {
        return gradeDatasource.findAll().stream().map(ReferenceDataRepository::toGrade).toList();
    }

    @Override
    public Optional<Grade> findGradeByCode(String code) {
        return gradeDatasource.findByCode(code).map(ReferenceDataRepository::toGrade);
    }

    @Override
    @Cacheable("teams")
    public List<Team> findAllTeams() {
        return teamDatasource.findAll().stream().map(ReferenceDataRepository::toTeam).toList();
    }

    @Override
    public Optional<Team> findTeamByCode(String code) {
        return teamDatasource.findByCode(code).map(ReferenceDataRepository::toTeam);
    }

    private static Grade toGrade(GradeEntity entity) {
        return new Grade(entity.getId(), entity.getPublicId(), entity.getCode(), entity.getName());
    }

    private static Team toTeam(TeamEntity entity) {
        return new Team(entity.getId(), entity.getPublicId(), entity.getCode(), entity.getName());
    }
}
