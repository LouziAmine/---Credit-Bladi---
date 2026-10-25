package com.credit.bladi.repository.reference;

import com.credit.bladi.model.reference.Grade;
import com.credit.bladi.model.reference.Team;
import java.util.List;
import java.util.Optional;

public interface IReferenceDataRepository {
    List<Grade> findAllGrades();

    Optional<Grade> findGradeByCode(String code);

    List<Team> findAllTeams();

    Optional<Team> findTeamByCode(String code);
}
