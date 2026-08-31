package com.credit.bladi.service.reference;

import com.credit.bladi.model.reference.Grade;
import com.credit.bladi.model.reference.Team;
import com.credit.bladi.repository.reference.IReferenceDataRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReferenceDataService implements IReferenceDataService {
    private final IReferenceDataRepository repository;

    public ReferenceDataService(IReferenceDataRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Grade> getGrades() {
        return repository.findAllGrades();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Team> getTeams() {
        return repository.findAllTeams();
    }
}
