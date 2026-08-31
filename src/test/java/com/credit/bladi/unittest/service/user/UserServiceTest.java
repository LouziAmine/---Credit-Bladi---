package com.credit.bladi.unittest.service.user;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_ANALYSTE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_DIRECTEUR;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_PAE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_UNKNOWN;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_ANALYST1_AT_BLADI_CREDIT_MA;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_ANALYSTE;
import static com.credit.bladi.unittest.mock.data.MockUsers.PASSWORD_PLAINTEXT;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ADMIN;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ALICE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ANALYST1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_CLIENT1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.exception.AuthException;
import com.credit.bladi.exception.ValidationException;
import com.credit.bladi.model.audit.AuditTarget;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.reference.Grade;
import com.credit.bladi.model.reference.Team;
import com.credit.bladi.model.user.User;
import com.credit.bladi.repository.DuplicateEntryException;
import com.credit.bladi.repository.reference.IReferenceDataRepository;
import com.credit.bladi.repository.user.IUserAdminRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import com.credit.bladi.service.user.UserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private IUserAdminRepository userRepository;

    @Mock
    private IReferenceDataRepository referenceDataRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IAuditService auditService;

    @Mock
    private ICurrentActorProvider currentActorProvider;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, referenceDataRepository, passwordEncoder, auditService,
            currentActorProvider);
    }

    @Test
    void shouldDelegateGetAllUsersToRepository() {
        User user = User.getBuilder().publicId(UUID.randomUUID()).id(1L).username(USER_ALICE).role(UserRole.CLIENT)
            .build();
        CreditPage<User> page = new CreditPage<>();
        page.setItems(List.of(user));
        page.setTotal(1);
        page.setCurrentPage(1);
        page.setSize(20);
        when(userRepository.findAll(1, 20)).thenReturn(page);

        CreditPage<User> result = userService.getAllUsers(1, 20);

        assertEquals(1, result.getItems().size());
        verify(userRepository).findAll(1, 20);
    }

    @Test
    void shouldEncodePasswordAndPersistUser() {
        User user = User.getBuilder().publicId(UUID.randomUUID())
            .username(USER_MANAGER1)
            .password(PASSWORD_PLAINTEXT)
            .role(UserRole.MANAGER)
            .build();
        UUID publicId = UUID.randomUUID();
        User saved = User.getBuilder().publicId(UUID.randomUUID())
            .id(99L)
            .publicId(publicId)
            .username(USER_MANAGER1)
            .password("$2a$encoded")
            .role(UserRole.MANAGER)
            .build();
        when(passwordEncoder.encode(PASSWORD_PLAINTEXT)).thenReturn("$2a$encoded");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UUID id = userService.createUser(user, null, null);

        assertEquals(publicId, id);
        verify(passwordEncoder).encode(PASSWORD_PLAINTEXT);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenCreatingUserWithExistingUsername() {
        User user = User.getBuilder().publicId(UUID.randomUUID()).username(USER_MANAGER1).password(PASSWORD_PLAINTEXT)
            .role(UserRole.MANAGER).build();
        when(userRepository.existsByUsername(USER_MANAGER1)).thenReturn(true);

        assertThrows(AuthException.class, () -> userService.createUser(user, null, null));
    }

    @Test
    void shouldDelegateFindByUsernameToRepository() {
        User user = User.getBuilder().publicId(UUID.randomUUID()).id(1L).username(USER_ALICE).role(UserRole.CLIENT)
            .build();
        when(userRepository.findByUsername(USER_ALICE)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername(USER_ALICE);

        assertEquals(user, result.get());
    }

    @Test
    void shouldThrowWhenUpdatingRoleOfUnknownUser() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AuthException.class, () -> userService.updateUserRole(99L, UserRole.CLIENT, USER_MANAGER1));
    }

    @Test
    void shouldThrowWhenCreateUserHitsUniqueConstraintRace() {
        User user = User.getBuilder().publicId(UUID.randomUUID()).username(USER_MANAGER1).password(PASSWORD_PLAINTEXT)
            .role(UserRole.MANAGER).build();
        when(passwordEncoder.encode(PASSWORD_PLAINTEXT)).thenReturn("$2a$encoded");
        when(userRepository.save(any(User.class)))
            .thenThrow(new DuplicateEntryException(new DataIntegrityViolationException("duplicate")));

        assertThrows(AuthException.class, () -> userService.createUser(user, null, null));
    }

    @Test
    void shouldReturnUserByIdWhenFound() {
        User user = User.getBuilder().publicId(UUID.randomUUID()).id(1L).username(USER_ALICE).role(UserRole.CLIENT)
            .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertEquals(user, result);
    }

    @Test
    void shouldThrowWhenUserByIdNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AuthException.class, () -> userService.getUserById(99L));
    }

    @Test
    void shouldDelegateToRepositoryWhenUpdatingRoleOfDifferentUser() {
        User targetUser = User.getBuilder().publicId(UUID.randomUUID())
            .id(42L)
            .username(USER_CLIENT1)
            .role(UserRole.CLIENT)
            .build();
        when(userRepository.findById(42L)).thenReturn(Optional.of(targetUser));

        userService.updateUserRole(42L, UserRole.CLIENT, USER_MANAGER1);

        verify(userRepository).updateRole(42L, UserRole.CLIENT);
    }

    @Test
    void shouldAllowReassigningManagerRoleToAnotherManagerUnaffectedByLastManagerCheck() {
        User targetManager = User.getBuilder().publicId(UUID.randomUUID())
            .id(7L)
            .username(USER_MANAGER2)
            .role(UserRole.MANAGER)
            .build();
        when(userRepository.findById(7L)).thenReturn(Optional.of(targetManager));

        userService.updateUserRole(7L, UserRole.MANAGER, USER_MANAGER1);

        verify(userRepository).updateRole(7L, UserRole.MANAGER);
    }

    @Test
    void shouldThrowWhenManagerTriesToModifyOwnRole() {
        User currentManager = User.getBuilder().publicId(UUID.randomUUID())
            .id(42L)
            .username(USER_MANAGER1)
            .role(UserRole.MANAGER)
            .build();
        when(userRepository.findById(42L)).thenReturn(Optional.of(currentManager));

        assertThrows(AuthException.class,
            () -> userService.updateUserRole(42L, UserRole.CLIENT, USER_MANAGER1));
    }

    @Test
    void shouldThrowWhenDemotingTheLastManager() {
        User lastManager = User.getBuilder().publicId(UUID.randomUUID())
            .id(7L)
            .username(USER_MANAGER2)
            .role(UserRole.MANAGER)
            .build();
        when(userRepository.findById(7L)).thenReturn(Optional.of(lastManager));
        when(userRepository.countByRole(UserRole.MANAGER)).thenReturn(1L);

        assertThrows(AuthException.class,
            () -> userService.updateUserRole(7L, UserRole.CLIENT, USER_MANAGER1));

        verify(userRepository, org.mockito.Mockito.never()).updateRole(7L, UserRole.CLIENT);
    }

    @Test
    void shouldAllowDemotingAManagerWhenAnotherManagerRemains() {
        User targetManager = User.getBuilder().publicId(UUID.randomUUID())
            .id(7L)
            .username(USER_MANAGER2)
            .role(UserRole.MANAGER)
            .build();
        when(userRepository.findById(7L)).thenReturn(Optional.of(targetManager));
        when(userRepository.countByRole(UserRole.MANAGER)).thenReturn(2L);

        userService.updateUserRole(7L, UserRole.CLIENT, USER_MANAGER1);

        verify(userRepository).updateRole(7L, UserRole.CLIENT);
    }

    @Test
    void shouldResolveGradeAndTeamCodesWhenCreatingUser() {
        User user = User.getBuilder().publicId(UUID.randomUUID()).username(USER_ANALYST1).password(PASSWORD_PLAINTEXT)
            .role(UserRole.MANAGER).build();
        User saved = User.getBuilder().publicId(UUID.randomUUID()).id(3L).username(USER_ANALYST1).role(UserRole.MANAGER)
            .build();
        when(passwordEncoder.encode(PASSWORD_PLAINTEXT)).thenReturn("$2a$encoded");
        when(referenceDataRepository.findGradeByCode(CODE_ANALYSTE))
            .thenReturn(Optional.of(new Grade(3L, UUID.randomUUID(), CODE_ANALYSTE, LABEL_ANALYSTE)));
        when(referenceDataRepository.findTeamByCode(CODE_PAE))
            .thenReturn(Optional.of(new Team(5L, UUID.randomUUID(), CODE_PAE, CODE_PAE)));
        when(userRepository.save(any(User.class))).thenReturn(saved);

        userService.createUser(user, CODE_ANALYSTE, CODE_PAE);

        verify(userRepository).save(argThat(u -> u.getGradeId().equals(3L) && u.getTeamId().equals(5L)));
    }

    @Test
    void shouldThrowWhenCreatingUserWithUnknownGradeCode() {
        User user = User.getBuilder().publicId(UUID.randomUUID()).username(USER_ANALYST1).password(PASSWORD_PLAINTEXT)
            .role(UserRole.MANAGER).build();
        when(referenceDataRepository.findGradeByCode(CODE_UNKNOWN)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> userService.createUser(user, CODE_UNKNOWN, null));

        verify(userRepository, org.mockito.Mockito.never()).save(any(User.class));
    }

    @Test
    void shouldUpdateProfileWhenUserExists() {
        User target = User.getBuilder().publicId(UUID.randomUUID()).id(3L).username(USER_ANALYST1)
            .role(UserRole.MANAGER).build();
        when(userRepository.findById(3L)).thenReturn(Optional.of(target));
        when(referenceDataRepository.findGradeByCode(CODE_ANALYSTE))
            .thenReturn(Optional.of(new Grade(3L, UUID.randomUUID(), CODE_ANALYSTE, LABEL_ANALYSTE)));
        when(referenceDataRepository.findTeamByCode(CODE_PAE))
            .thenReturn(Optional.of(new Team(5L, UUID.randomUUID(), CODE_PAE, CODE_PAE)));

        userService.updateUserProfile(3L, CODE_ANALYSTE, CODE_PAE, EMAIL_ANALYST1_AT_BLADI_CREDIT_MA, USER_ADMIN);

        verify(userRepository).updateProfile(3L, 3L, 5L, EMAIL_ANALYST1_AT_BLADI_CREDIT_MA);
        verify(auditService).log(eq(USER_ADMIN), eq(AuditAction.USER_PROFILE_UPDATE), any(AuditTarget.class),
            argThat(details -> details.contains("gradeId: none -> 3") && details.contains("teamId: none -> 5")
                && !details.contains(EMAIL_ANALYST1_AT_BLADI_CREDIT_MA)));
    }

    @Test
    void shouldRejectSelfProfileUpdate() {
        User self = User.getBuilder().publicId(UUID.randomUUID()).id(3L).username(USER_ANALYST1)
            .role(UserRole.MANAGER).build();
        when(userRepository.findById(3L)).thenReturn(Optional.of(self));

        AuthException ex = assertThrows(AuthException.class,
            () -> userService.updateUserProfile(3L, CODE_DIRECTEUR, CODE_PAE, null, USER_ANALYST1));

        assertEquals(AuthException.AuthExceptionType.SELF_PROFILE_MODIFICATION_FORBIDDEN, ex.getType());
        verify(userRepository, never()).updateProfile(any(), any(), any(), any());
        verify(auditService, never()).log(any(), any(), any(), any());
    }

    @Test
    void shouldThrowWhenUpdatingProfileOfUnknownUser() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AuthException.class, () -> userService.updateUserProfile(99L, CODE_ANALYSTE, CODE_PAE, null,
            USER_ADMIN));
    }
}
