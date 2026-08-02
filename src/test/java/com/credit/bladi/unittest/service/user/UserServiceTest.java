package com.credit.bladi.unittest.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.exception.AuthException;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.user.User;
import com.credit.bladi.repository.user.IUserRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.user.UserService;
import java.util.List;
import java.util.Optional;
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
    private IUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IAuditService auditService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder, auditService);
    }

    @Test
    void shouldDelegateGetAllUsersToRepository() {
        User user = User.getBuilder().id(1L).username("alice").role(UserRole.CLIENT).build();
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
        User user = User.getBuilder()
            .username("manager1")
            .password("plaintext")
            .role(UserRole.MANAGER)
            .build();
        User saved = User.getBuilder()
            .id(99L)
            .username("manager1")
            .password("$2a$encoded")
            .role(UserRole.MANAGER)
            .build();
        when(passwordEncoder.encode("plaintext")).thenReturn("$2a$encoded");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        Long id = userService.createUser(user);

        assertEquals(99L, id);
        verify(passwordEncoder).encode("plaintext");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenCreatingUserWithExistingUsername() {
        User user = User.getBuilder().username("manager1").password("plaintext").role(UserRole.MANAGER).build();
        when(userRepository.existsByUsername("manager1")).thenReturn(true);

        assertThrows(AuthException.class, () -> userService.createUser(user));
    }

    @Test
    void shouldDelegateFindByUsernameToRepository() {
        User user = User.getBuilder().id(1L).username("alice").role(UserRole.CLIENT).build();
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("alice");

        assertEquals(user, result.get());
    }

    @Test
    void shouldThrowWhenUpdatingRoleOfUnknownUser() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AuthException.class, () -> userService.updateUserRole(99L, UserRole.CLIENT, "manager1"));
    }

    @Test
    void shouldThrowWhenCreateUserHitsUniqueConstraintRace() {
        User user = User.getBuilder().username("manager1").password("plaintext").role(UserRole.MANAGER).build();
        when(passwordEncoder.encode("plaintext")).thenReturn("$2a$encoded");
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("duplicate"));

        assertThrows(AuthException.class, () -> userService.createUser(user));
    }

    @Test
    void shouldReturnUserByIdWhenFound() {
        User user = User.getBuilder().id(1L).username("alice").role(UserRole.CLIENT).build();
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
        User targetUser = User.getBuilder()
            .id(42L)
            .username("client1")
            .role(UserRole.CLIENT)
            .build();
        when(userRepository.findById(42L)).thenReturn(Optional.of(targetUser));

        userService.updateUserRole(42L, UserRole.CLIENT, "manager1");

        verify(userRepository).updateRole(42L, UserRole.CLIENT);
    }

    @Test
    void shouldAllowReassigningManagerRoleToAnotherManagerUnaffectedByLastManagerCheck() {
        User targetManager = User.getBuilder()
            .id(7L)
            .username("manager2")
            .role(UserRole.MANAGER)
            .build();
        when(userRepository.findById(7L)).thenReturn(Optional.of(targetManager));

        userService.updateUserRole(7L, UserRole.MANAGER, "manager1");

        verify(userRepository).updateRole(7L, UserRole.MANAGER);
    }

    @Test
    void shouldThrowWhenManagerTriesToModifyOwnRole() {
        User currentManager = User.getBuilder()
            .id(42L)
            .username("manager1")
            .role(UserRole.MANAGER)
            .build();
        when(userRepository.findById(42L)).thenReturn(Optional.of(currentManager));

        assertThrows(AuthException.class,
            () -> userService.updateUserRole(42L, UserRole.CLIENT, "manager1"));
    }

    @Test
    void shouldThrowWhenDemotingTheLastManager() {
        User lastManager = User.getBuilder()
            .id(7L)
            .username("manager2")
            .role(UserRole.MANAGER)
            .build();
        when(userRepository.findById(7L)).thenReturn(Optional.of(lastManager));
        when(userRepository.countByRole(UserRole.MANAGER)).thenReturn(1L);

        assertThrows(AuthException.class,
            () -> userService.updateUserRole(7L, UserRole.CLIENT, "manager1"));

        verify(userRepository, org.mockito.Mockito.never()).updateRole(7L, UserRole.CLIENT);
    }

    @Test
    void shouldAllowDemotingAManagerWhenAnotherManagerRemains() {
        User targetManager = User.getBuilder()
            .id(7L)
            .username("manager2")
            .role(UserRole.MANAGER)
            .build();
        when(userRepository.findById(7L)).thenReturn(Optional.of(targetManager));
        when(userRepository.countByRole(UserRole.MANAGER)).thenReturn(2L);

        userService.updateUserRole(7L, UserRole.CLIENT, "manager1");

        verify(userRepository).updateRole(7L, UserRole.CLIENT);
    }
}
