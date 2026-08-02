package com.credit.bladi.unittest.controller.user.mapper;

import static com.credit.bladi.unittest.mock.user.MockUser.PASSWORD;
import static com.credit.bladi.unittest.mock.user.MockUser.ROLE;
import static com.credit.bladi.unittest.mock.user.MockUser.USERNAME;
import static com.credit.bladi.unittest.mock.user.MockUser.createRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.controller.user.mapper.UserControllerMapper;
import com.credit.bladi.exception.ValidationException;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.user.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Test;

class UserControllerMapperTest {

    @Test
    void shouldMapRoleStringToEnum() {
        assertEquals(UserRole.MANAGER, UserControllerMapper.toRole("MANAGER"));
    }

    @Test
    void shouldThrowCuratedValidationExceptionForUnknownRole() {
        ValidationException ex = assertThrows(ValidationException.class,
            () -> UserControllerMapper.toRole("NOPE"));
        assertEquals("INVALID_VALUE: role: NOPE", ex.getMessage());
    }

    @Test
    void shouldMapCreateRequestToUser() {
        User user = UserControllerMapper.toUser(createRequest());

        User expected = User.getBuilder()
            .username(USERNAME)
            .password(PASSWORD)
            .role(ROLE)
            .build();

        assertTrue(EqualsBuilder.reflectionEquals(expected, user));
    }
}