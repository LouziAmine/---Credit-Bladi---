package com.credit.bladi.unittest.datasource.user.mapper;

import static com.credit.bladi.unittest.mock.data.MockTokens.TOKEN_ENCODED;
import static com.credit.bladi.unittest.mock.data.MockUsers.PASSWORD_PASS;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ALICE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_BOB;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.user.entity.UserEntity;
import com.credit.bladi.datasource.user.mapper.UserEntityMapper;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.user.User;
import java.lang.reflect.Constructor;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Test;

class UserEntityMapperTest {
    @Test
    void shouldMapFromEntity() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setUsername(USER_ALICE);
        entity.setPassword(PASSWORD_PASS);
        entity.setRole(UserRole.CLIENT);

        User user = UserEntityMapper.mapFromEntity(entity);

        User expected = User.getBuilder().id(1L).username(USER_ALICE).password(PASSWORD_PASS)
            .role(UserRole.CLIENT).build();
        assertTrue(EqualsBuilder.reflectionEquals(expected, user));
    }

    @Test
    void shouldMapEntitiesToPage() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setUsername(USER_ALICE);
        entity.setRole(UserRole.CLIENT);

        CreditPage<User> page = UserEntityMapper.mapToPage(List.of(entity), 10L, 1, 3);

        assertEquals(1, page.getItems().size());
        assertEquals(4, page.getLastPage());
    }

    @Test
    void shouldMapToPageWithZeroSizeYieldsZeroLastPage() {
        CreditPage<User> page = UserEntityMapper.mapToPage(List.of(), 10L, 1, 0);

        assertEquals(0, page.getLastPage());
    }

    @Test
    void shouldMapToNewEntity() {
        User user = User.getBuilder()
            .username(USER_BOB)
            .password(TOKEN_ENCODED)
            .role(UserRole.MANAGER)
            .build();

        UserEntity entity = UserEntityMapper.mapToNewEntity(user);

        UserEntity expected = new UserEntity();
        expected.setUsername(USER_BOB);
        expected.setPassword(TOKEN_ENCODED);
        expected.setRole(UserRole.MANAGER);
        assertTrue(EqualsBuilder.reflectionEquals(expected, entity));
    }

    @Test
    void shouldRejectMapToNewEntityWithExistingId() {
        User user = User.getBuilder()
            .id(2L)
            .username(USER_BOB)
            .password(TOKEN_ENCODED)
            .role(UserRole.MANAGER)
            .build();

        assertThrows(IllegalArgumentException.class, () -> UserEntityMapper.mapToNewEntity(user));
    }

    @Test
    void shouldCoverPrivateConstructor() throws Exception {
        Constructor<UserEntityMapper> constructor = UserEntityMapper.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        UserEntityMapper instance = constructor.newInstance();
        assertNotNull(instance);
    }
}
