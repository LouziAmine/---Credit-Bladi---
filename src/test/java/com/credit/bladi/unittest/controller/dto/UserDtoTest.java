package com.credit.bladi.unittest.controller.dto;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CLIENT;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_MANAGER;
import static com.credit.bladi.unittest.mock.data.MockUsers.PASSWORD_PASS;
import static com.credit.bladi.unittest.mock.data.MockUsers.PASSWORD_SECUREPASS;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ALICE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_BOB;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_NEWUSER;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_OTHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.controller.dto.user.request.CreateUserRequestDTO;
import com.credit.bladi.controller.dto.user.request.UpdateUserRoleRequestDTO;
import org.junit.jupiter.api.Test;

class UserDtoTest {
    @Test
    void shouldSetAndGetCreateUserRequestFields() {
        CreateUserRequestDTO dto = new CreateUserRequestDTO(
            USER_NEWUSER, null, null, PASSWORD_SECUREPASS, CODE_CLIENT, null, null);

        assertEquals(USER_NEWUSER, dto.username());
        assertEquals(PASSWORD_SECUREPASS, dto.password());
        assertEquals(CODE_CLIENT, dto.role());
    }

    @Test
    void shouldCreateUserRequestEqualsAndHashCode() {
        CreateUserRequestDTO d1 = new CreateUserRequestDTO(USER_ALICE, null, null, PASSWORD_PASS, CODE_CLIENT, null,
            null);
        CreateUserRequestDTO d2 = new CreateUserRequestDTO(USER_ALICE, null, null, PASSWORD_PASS, CODE_CLIENT, null,
            null);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldCreateUserRequestNotEqualWhenDifferentFields() {
        CreateUserRequestDTO d1 = new CreateUserRequestDTO(USER_ALICE, null, null, null, null, null, null);
        CreateUserRequestDTO d2 = new CreateUserRequestDTO(USER_BOB, null, null, null, null, null, null);

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldCreateUserRequestNotEqualToDifferentType() {
        CreateUserRequestDTO dto = new CreateUserRequestDTO(null, null, null, null, null, null, null);
        assertFalse(dto.equals(USER_OTHER));
    }

    @Test
    void shouldCreateUserRequestToString() {
        CreateUserRequestDTO dto = new CreateUserRequestDTO(USER_ALICE, null, null, null, CODE_CLIENT, null, null);
        String str = dto.toString();
        assertTrue(str.contains(USER_ALICE));
        assertTrue(str.contains(CODE_CLIENT));
    }

    @Test
    void shouldSetAndGetUpdateUserRoleRequestField() {
        UpdateUserRoleRequestDTO dto = new UpdateUserRoleRequestDTO(CODE_MANAGER);

        assertEquals(CODE_MANAGER, dto.role());
    }

    @Test
    void shouldUpdateUserRoleRequestEqualsAndHashCode() {
        UpdateUserRoleRequestDTO d1 = new UpdateUserRoleRequestDTO(CODE_MANAGER);
        UpdateUserRoleRequestDTO d2 = new UpdateUserRoleRequestDTO(CODE_MANAGER);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldUpdateUserRoleRequestNotEqualWhenDifferentFields() {
        UpdateUserRoleRequestDTO d1 = new UpdateUserRoleRequestDTO(CODE_MANAGER);
        UpdateUserRoleRequestDTO d2 = new UpdateUserRoleRequestDTO(CODE_CLIENT);

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldUpdateUserRoleRequestNotEqualToDifferentType() {
        UpdateUserRoleRequestDTO dto = new UpdateUserRoleRequestDTO(null);
        assertFalse(dto.equals(USER_OTHER));
    }

    @Test
    void shouldUpdateUserRoleRequestToString() {
        UpdateUserRoleRequestDTO dto = new UpdateUserRoleRequestDTO(CODE_MANAGER);
        String str = dto.toString();
        assertTrue(str.contains(CODE_MANAGER));
    }
}
