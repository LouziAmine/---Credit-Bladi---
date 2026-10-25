package com.credit.bladi.service.reference;

import com.credit.bladi.model.reference.Grade;
import com.credit.bladi.model.reference.Team;
import java.util.List;

public interface IReferenceDataService {
    List<Grade> getGrades();

    List<Team> getTeams();
}
