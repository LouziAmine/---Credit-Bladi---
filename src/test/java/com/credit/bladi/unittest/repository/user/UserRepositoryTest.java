package com.credit.bladi.unittest.repository.user;

import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_JOHN_DOE_AT_BLADI_CREDIT_MA;
import static com.credit.bladi.unittest.mock.data.MockUsers.PASSWORD_PASS;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ALICE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ANALYST1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_CAROL;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_UNKNOWN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.user.UserDatasource;
import com.credit.bladi.datasource.user.entity.UserEntity;
import com.credit.bladi.exception.AuthException;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.user.User;
import com.credit.bladi.repository.reference.IReferenceDataRepository;
import com.credit.bladi.repository.user.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {
    @Mock
    private UserDatasource datasource;

    @Mock
    private IReferenceDataRepository referenceDataRepository;

    private UserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new UserRepository(datasource, referenceDataRepository);
    }

    private UserEntity buildEntity(Long id, String username, String password, UserRole role) {
        UserEntity entity = new UserEntity();
        entity.setId(id);
        entity.setUsername(username);
        entity.setPassword(password);
        entity.setRole(role);
        return entity;
    }

    @Test
    void shouldFindAllPagedResults() {
        UserEntity entity = buildEntity(1L, USER_ALICE, PASSWORD_PASS, UserRole.CLIENT);
        when(datasource.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(entity)));

        CreditPage<User> result = repository.findAll(1, 20);

        assertEquals(1, result.getItems().size());
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getCurrentPage());
        assertEquals(20, result.getSize());
    }

    @Test
    void shouldFindUserByUsername() {
        UserEntity entity = buildEntity(1L, USER_ALICE, PASSWORD_PASS, UserRole.CLIENT);
        when(datasource.findByUsername(USER_ALICE)).thenReturn(Optional.of(entity));

        Optional<User> result = repository.findByUsername(USER_ALICE);

        assertTrue(result.isPresent());
        assertEquals(USER_ALICE, result.get().getUsername());
    }

    @Test
    void shouldReturnEmptyWhenUsernameNotFound() {
        when(datasource.findByUsername(USER_UNKNOWN)).thenReturn(Optional.empty());

        Optional<User> result = repository.findByUsername(USER_UNKNOWN);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldSaveUser() {
        User user = User.getBuilder().username(USER_CAROL).password("pw").role(UserRole.CLIENT).build();
        UserEntity savedEntity = buildEntity(10L, USER_CAROL, "pw", UserRole.CLIENT);
        when(datasource.saveAndFlush(any(UserEntity.class))).thenReturn(savedEntity);

        User saved = repository.save(user);

        assertEquals(USER_CAROL, saved.getUsername());
        assertEquals(10L, saved.getId());
    }

    @Test
    void shouldFindUserById() {
        UserEntity entity = buildEntity(1L, USER_ALICE, PASSWORD_PASS, UserRole.CLIENT);
        when(datasource.findById(1L)).thenReturn(Optional.of(entity));

        Optional<User> result = repository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(USER_ALICE, result.get().getUsername());
    }

    @Test
    void shouldReturnEmptyWhenIdNotFound() {
        when(datasource.findById(99L)).thenReturn(Optional.empty());

        Optional<User> result = repository.findById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldCountByRole() {
        when(datasource.countByRole(UserRole.MANAGER)).thenReturn(2L);

        long count = repository.countByRole(UserRole.MANAGER);

        assertEquals(2L, count);
    }

    @Test
    void shouldCheckExistsByUsername() {
        when(datasource.existsByUsername(USER_CAROL)).thenReturn(true);

        boolean exists = repository.existsByUsername(USER_CAROL);

        assertTrue(exists);
    }

    @Test
    void shouldUpdateRole() {
        when(datasource.updateRole(1L, UserRole.MANAGER)).thenReturn(1);

        repository.updateRole(1L, UserRole.MANAGER);

        verify(datasource).updateRole(1L, UserRole.MANAGER);
    }

    @Test
    void shouldThrowWhenUserNotFoundForRoleUpdate() {
        when(datasource.updateRole(99L, UserRole.MANAGER)).thenReturn(0);

        assertThrows(AuthException.class, () -> repository.updateRole(99L, UserRole.MANAGER));
    }

    @Test
    void shouldUpdateProfile() {
        when(datasource.updateProfile(1L, 3L, 5L, EMAIL_JOHN_DOE_AT_BLADI_CREDIT_MA)).thenReturn(1);

        repository.updateProfile(1L, 3L, 5L, EMAIL_JOHN_DOE_AT_BLADI_CREDIT_MA);

        verify(datasource).updateProfile(1L, 3L, 5L, EMAIL_JOHN_DOE_AT_BLADI_CREDIT_MA);
    }

    @Test
    void shouldThrowWhenUserNotFoundForProfileUpdate() {
        when(datasource.updateProfile(99L, 3L, 5L, null)).thenReturn(0);

        assertThrows(AuthException.class, () -> repository.updateProfile(99L, 3L, 5L, null));
    }

    @Test
    void shouldFindManagersByGradeAndTeam() {
        UserEntity entity = buildEntity(1L, USER_ANALYST1, PASSWORD_PASS, UserRole.MANAGER);
        when(datasource.findByRoleAndGradeIdAndTeamId(UserRole.MANAGER, 3L, 5L)).thenReturn(List.of(entity));

        List<User> result = repository.findManagersByGradeIdAndTeamId(3L, 5L);

        assertEquals(1, result.size());
        assertEquals(USER_ANALYST1, result.get(0).getUsername());
    }
}
