package com.credit.bladi.service.user;

import static com.credit.bladi.exception.AuthException.AuthExceptionType.LAST_MANAGER_DEMOTION_FORBIDDEN;
import static com.credit.bladi.exception.AuthException.AuthExceptionType.USERNAME_TAKEN;
import static com.credit.bladi.exception.AuthException.AuthExceptionType.USER_NOT_FOUND;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.exception.AuthException;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.user.User;
import com.credit.bladi.repository.user.IUserRepository;
import com.credit.bladi.service.audit.IAuditService;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements IUserService {

    private static final Logger LOGGER = LogManager.getLogger(UserService.class);

    private final IUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final IAuditService auditService;

    public UserService(IUserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       IAuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    @Override
    @Transactional(readOnly = true)
    public CreditPage<User> getAllUsers(int page, int size) {
        LOGGER.info("GetAllUsers - page={}, size={}", page, size);
        return userRepository.findAll(page, size);
    }

    @Override
    @Transactional
    public Long createUser(User user) {
        LOGGER.info("CreateUser - role={}, username={}", user.getRole(), user.getUsername());
        if (userRepository.existsByUsername(user.getUsername())) {
            throw AuthException.of(USERNAME_TAKEN);
        }
        User saved;
        try {
            saved = userRepository.save(User.getBuilder()
                .username(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .role(user.getRole())
                .build());
        } catch (DataIntegrityViolationException e) {
            throw AuthException.of(USERNAME_TAKEN);
        }
        auditService.log(user.getUsername(), AuditAction.USER_CREATE, "role=" + user.getRole());
        return saved.getId();
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
            "userId=" + id + ", role=" + role);
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