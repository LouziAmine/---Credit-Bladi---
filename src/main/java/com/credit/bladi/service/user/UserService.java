package com.credit.bladi.service.user;

import static com.credit.bladi.exception.AuthException.AuthExceptionType.LAST_MANAGER_DEMOTION_FORBIDDEN;
import static com.credit.bladi.exception.AuthException.AuthExceptionType.USERNAME_TAKEN;
import static com.credit.bladi.exception.AuthException.AuthExceptionType.USER_NOT_FOUND;
import static com.credit.bladi.exception.ValidationException.ValidationExceptionType.INVALID_VALUE;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.exception.AuthException;
import com.credit.bladi.exception.ValidationException;
import com.credit.bladi.model.audit.AuditChanges;
import com.credit.bladi.model.audit.AuditTarget;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.AuditEntityType;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.user.User;
import com.credit.bladi.repository.DuplicateEntryException;
import com.credit.bladi.repository.reference.IReferenceDataRepository;
import com.credit.bladi.repository.user.IUserAdminRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements IUserService {
    private static final Logger LOGGER = LogManager.getLogger(UserService.class);

    private final IUserAdminRepository userRepository;

    private final IReferenceDataRepository referenceDataRepository;

    private final PasswordEncoder passwordEncoder;

    private final IAuditService auditService;

    private final ICurrentActorProvider currentActorProvider;

    public UserService(IUserAdminRepository userRepository,
                       IReferenceDataRepository referenceDataRepository,
                       PasswordEncoder passwordEncoder,
                       IAuditService auditService,
                       ICurrentActorProvider currentActorProvider) {
        this.userRepository = userRepository;
        this.referenceDataRepository = referenceDataRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
        this.currentActorProvider = currentActorProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public CreditPage<User> getAllUsers(int page, int size) {
        LOGGER.info("GetAllUsers - page={}, size={}", page, size);
        return userRepository.findAll(page, size);
    }

    @Override
    @Transactional
    public UUID createUser(User user, String gradeCode, String teamCode) {
        LOGGER.info("CreateUser - role={}, username={}", user.getRole(), user.getUsername());
        if (userRepository.existsByUsername(user.getUsername())) {
            throw AuthException.of(USERNAME_TAKEN);
        }
        Long gradeId = resolveGradeId(gradeCode);
        Long teamId = resolveTeamId(teamCode);
        User saved;
        try {
            saved = userRepository.save(User.getBuilder()
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .role(user.getRole())
                .gradeId(gradeId)
                .teamId(teamId)
                .build());
        } catch (DuplicateEntryException e) {
            throw AuthException.of(USERNAME_TAKEN, e);
        }
        auditService.log(currentActorProvider.getCurrentUsername(), AuditAction.USER_CREATE,
            new AuditTarget(AuditEntityType.USER, saved.getPublicId()),
            "username=" + user.getUsername() + ", role=" + user.getRole());
        return saved.getPublicId();
    }

    @Override
    @Transactional(readOnly = true)
    public Long resolveUserId(UUID publicId) {
        return userRepository.findIdByPublicId(publicId)
            .orElseThrow(() -> AuthException.of(USER_NOT_FOUND));
    }

    @Override
    @Transactional
    public void updateUserRole(Long id, UserRole role, String currentUsername) {
        LOGGER.info("UpdateUserRole - userId={}, role={}", id, role);
        User target = userRepository.findById(id)
            .orElseThrow(() -> AuthException.of(USER_NOT_FOUND));
        target.assertRoleModifiableBy(currentUsername);
        if (target.getRole() == UserRole.MANAGER && role != UserRole.MANAGER
                && userRepository.countByRole(UserRole.MANAGER) <= 1) {
            throw AuthException.of(LAST_MANAGER_DEMOTION_FORBIDDEN);
        }
        userRepository.updateRole(id, role);
        auditService.log(currentUsername, AuditAction.USER_ROLE_UPDATE,
            new AuditTarget(AuditEntityType.USER, target.getPublicId()),
            "userId=" + id + ", " + AuditChanges.of().field("role", target.getRole(), role));
    }

    @Override
    @Transactional
    public void updateUserProfile(Long id, String gradeCode, String teamCode, String email, String currentUsername) {
        LOGGER.info("UpdateUserProfile - userId={}, gradeCode={}, teamCode={}", id, gradeCode, teamCode);
        User target = userRepository.findById(id).orElseThrow(() -> AuthException.of(USER_NOT_FOUND));
        target.assertProfileModifiableBy(currentUsername);
        Long gradeId = resolveGradeId(gradeCode);
        Long teamId = resolveTeamId(teamCode);
        userRepository.updateProfile(id, gradeId, teamId, email);
        auditService.log(currentUsername, AuditAction.USER_PROFILE_UPDATE,
            new AuditTarget(AuditEntityType.USER, target.getPublicId()),
            "userId=" + id + ", " + AuditChanges.of()
                .field("gradeId", target.getGradeId(), gradeId)
                .field("teamId", target.getTeamId(), teamId)
                .field("emailChanged", false, !Objects.equals(target.getEmail(), email)));
    }

    private Long resolveGradeId(String gradeCode) {
        if (gradeCode == null) {
            return null;
        }
        return referenceDataRepository.findGradeByCode(gradeCode)
            .orElseThrow(() -> ValidationException.of(INVALID_VALUE, "gradeCode: " + gradeCode))
            .id();
    }

    private Long resolveTeamId(String teamCode) {
        if (teamCode == null) {
            return null;
        }
        return referenceDataRepository.findTeamByCode(teamCode)
            .orElseThrow(() -> ValidationException.of(INVALID_VALUE, "teamCode: " + teamCode))
            .id();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> AuthException.of(USER_NOT_FOUND));
    }
}
