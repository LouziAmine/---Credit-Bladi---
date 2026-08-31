package com.credit.bladi.repository.user;

import static com.credit.bladi.exception.AuthException.AuthExceptionType.USER_NOT_FOUND;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.user.UserDatasource;
import com.credit.bladi.datasource.user.entity.UserEntity;
import com.credit.bladi.datasource.user.mapper.UserEntityMapper;
import com.credit.bladi.exception.AuthException;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.reference.Grade;
import com.credit.bladi.model.reference.Team;
import com.credit.bladi.model.user.User;
import com.credit.bladi.repository.DuplicateEntryException;
import com.credit.bladi.repository.reference.IReferenceDataRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository implements IUserAdminRepository {
    private static final Logger LOGGER = LogManager.getLogger(UserRepository.class);

    private final UserDatasource datasource;

    private final IReferenceDataRepository referenceDataRepository;

    public UserRepository(UserDatasource datasource, IReferenceDataRepository referenceDataRepository) {
        this.datasource = datasource;
        this.referenceDataRepository = referenceDataRepository;
    }

    @Override
    public CreditPage<User> findAll(int page, int size) {
        LOGGER.debug("findAll - page: {}, size: {}", page, size);
        Page<UserEntity> result = datasource.findAll(PageRequest.of(page - 1, size, Sort.by("id")));
        Map<Long, Grade> gradesById = indexById(referenceDataRepository.findAllGrades(), Grade::id);
        Map<Long, Team> teamsById = indexById(referenceDataRepository.findAllTeams(), Team::id);
        List<User> items = result.getContent().stream()
            .map(entity -> enrich(UserEntityMapper.mapFromEntity(entity), gradesById, teamsById))
            .toList();
        return CreditPage.of(items, page, size, result.getTotalElements());
    }

    @Override
    public Optional<User> findByUsername(String username) {
        LOGGER.debug("findByUsername - username: {}", username);
        return datasource.findByUsername(username).map(UserEntityMapper::mapFromEntity).map(this::enrich);
    }

    @Override
    public Optional<User> findById(Long id) {
        LOGGER.debug("findById - id: {}", id);
        return datasource.findById(id).map(UserEntityMapper::mapFromEntity).map(this::enrich);
    }

    @Override
    public List<User> findAllById(List<Long> ids) {
        Map<Long, Grade> gradesById = indexById(referenceDataRepository.findAllGrades(), Grade::id);
        Map<Long, Team> teamsById = indexById(referenceDataRepository.findAllTeams(), Team::id);
        return datasource.findAllById(ids).stream()
            .map(entity -> enrich(UserEntityMapper.mapFromEntity(entity), gradesById, teamsById))
            .toList();
    }

    @Override
    public Optional<Long> findIdByPublicId(UUID publicId) {
        LOGGER.debug("findIdByPublicId - publicId: {}", publicId);
        return datasource.findByPublicId(publicId).map(UserEntity::getId);
    }

    @Override
    public boolean existsByUsername(String username) {
        return datasource.existsByUsername(username);
    }

    @Override
    public long countByRole(UserRole role) {
        return datasource.countByRole(role);
    }

    @Override
    public List<User> findManagersByGradeIdAndTeamId(Long gradeId, Long teamId) {
        return datasource.findByRoleAndGradeIdAndTeamId(UserRole.MANAGER, gradeId, teamId).stream()
            .map(UserEntityMapper::mapFromEntity)
            .map(this::enrich)
            .toList();
    }

    @Override
    public List<User> findManagersByTeamId(Long teamId) {
        return datasource.findByRoleAndTeamId(UserRole.MANAGER, teamId).stream()
            .map(UserEntityMapper::mapFromEntity)
            .map(this::enrich)
            .toList();
    }

    @Override
    public List<User> findAllByRole(UserRole role) {
        return datasource.findByRole(role).stream()
            .map(UserEntityMapper::mapFromEntity)
            .map(this::enrich)
            .toList();
    }

    @Override
    public List<User> findClientsByEmail(String email) {
        return datasource.findByRoleAndEmail(UserRole.CLIENT, email).stream()
            .map(UserEntityMapper::mapFromEntity)
            .map(this::enrich)
            .toList();
    }

    @Override
    public User save(User user) {
        User saved;
        try {
            saved = enrich(UserEntityMapper.mapFromEntity(
                datasource.saveAndFlush(UserEntityMapper.mapToNewEntity(user))));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEntryException(e);
        }
        LOGGER.debug("save - username: {}, role: {}", user.getUsername(), user.getRole());
        return saved;
    }

    @Override
    public void updateRole(Long id, UserRole role) {
        int updated = datasource.updateRole(id, role);
        if (updated == 0) {
            throw AuthException.of(USER_NOT_FOUND);
        }
        LOGGER.debug("updateRole - userId: {}, role: {}", id, role);
    }

    @Override
    public void updateProfile(Long id, Long gradeId, Long teamId, String email) {
        int updated = datasource.updateProfile(id, gradeId, teamId, email);
        if (updated == 0) {
            throw AuthException.of(USER_NOT_FOUND);
        }
        LOGGER.debug("updateProfile - userId: {}, gradeId: {}, teamId: {}", id, gradeId, teamId);
    }

    private User enrich(User user) {
        Map<Long, Grade> gradesById = indexById(referenceDataRepository.findAllGrades(), Grade::id);
        Map<Long, Team> teamsById = indexById(referenceDataRepository.findAllTeams(), Team::id);
        return enrich(user, gradesById, teamsById);
    }

    private static User enrich(User user, Map<Long, Grade> gradesById, Map<Long, Team> teamsById) {
        Grade grade = user.getGradeId() != null ? gradesById.get(user.getGradeId()) : null;
        Team team = user.getTeamId() != null ? teamsById.get(user.getTeamId()) : null;
        return User.getBuilder()
            .id(user.getId())
            .publicId(user.getPublicId())
            .username(user.getUsername())
            .fullName(user.getFullName())
            .email(user.getEmail())
            .password(user.getPassword())
            .role(user.getRole())
            .gradeId(user.getGradeId())
            .gradeCode(grade != null ? grade.code() : null)
            .gradeName(grade != null ? grade.name() : null)
            .teamId(user.getTeamId())
            .teamCode(team != null ? team.code() : null)
            .teamName(team != null ? team.name() : null)
            .preferredLanguage(user.getPreferredLanguage())
            .build();
    }

    private static <T> Map<Long, T> indexById(List<T> items, Function<T, Long> idExtractor) {
        return items.stream().collect(Collectors.toMap(idExtractor, Function.identity()));
    }
}
