package com.credit.bladi.unittest.datasource.user.mapper;

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
        entity.setUsername("alice");
        entity.setPassword("pass");
        entity.setRole(UserRole.CLIENT);

        User user = UserEntityMapper.mapFromEntity(entity);

        User expected = User.getBuilder().id(1L).username("alice").password("pass").role(UserRole.CLIENT).build();
        assertTrue(EqualsBuilder.reflectionEquals(expected, user));
    }

    @Test
    void shouldMapEntitiesToPage() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setUsername("alice");
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
            .username("bob")
            .password("encoded")
            .role(UserRole.MANAGER)
            .build();

        UserEntity entity = UserEntityMapper.mapToNewEntity(user);

        UserEntity expected = new UserEntity();
        expected.setUsername("bob");
        expected.setPassword("encoded");
        expected.setRole(UserRole.MANAGER);
        assertTrue(EqualsBuilder.reflectionEquals(expected, entity));
    }

    @Test
    void shouldRejectMapToNewEntityWithExistingId() {
        User user = User.getBuilder()
            .id(2L)
            .username("bob")
            .password("encoded")
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