package com.credit.bladi.unittest.controller.dto;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_MANAGER;
import static com.credit.bladi.unittest.mock.data.MockUsers.PASSWORD_NEWPASS;
import static com.credit.bladi.unittest.mock.data.MockUsers.PASSWORD_PASS;
import static com.credit.bladi.unittest.mock.data.MockUsers.PASSWORD_SECRET;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ALICE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_BOB;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JOHNDOE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_NEWUSER;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_OTHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.controller.dto.auth.request.LoginRequestDTO;
import com.credit.bladi.controller.dto.auth.request.RegisterRequestDTO;
import com.credit.bladi.controller.dto.auth.response.AuthUserResponseDTO;
import org.junit.jupiter.api.Test;

class AuthDtoTest {
    @Test
    void shouldSetAndGetLoginRequestFields() {
        LoginRequestDTO dto = new LoginRequestDTO(USER_JOHNDOE, PASSWORD_SECRET);

        assertEquals(USER_JOHNDOE, dto.username());
        assertEquals(PASSWORD_SECRET, dto.password());
    }

    @Test
    void shouldLoginRequestEqualsAndHashCode() {
        LoginRequestDTO d1 = new LoginRequestDTO(USER_ALICE, PASSWORD_PASS);
        LoginRequestDTO d2 = new LoginRequestDTO(USER_ALICE, PASSWORD_PASS);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldLoginRequestNotBeEqualWhenDifferentFields() {
        LoginRequestDTO d1 = new LoginRequestDTO(USER_ALICE, null);
        LoginRequestDTO d2 = new LoginRequestDTO(USER_BOB, null);

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldLoginRequestNotBeEqualToDifferentType() {
        LoginRequestDTO dto = new LoginRequestDTO(null, null);
        assertFalse(dto.equals(USER_OTHER));
    }

    @Test
    void shouldLoginRequestToString() {
        LoginRequestDTO dto = new LoginRequestDTO(USER_ALICE, null);
        String str = dto.toString();
        assertTrue(str.contains(USER_ALICE));
    }

    @Test
    void shouldSetAndGetRegisterRequestFields() {
        RegisterRequestDTO dto = new RegisterRequestDTO(USER_NEWUSER, PASSWORD_NEWPASS);

        assertEquals(USER_NEWUSER, dto.username());
        assertEquals(PASSWORD_NEWPASS, dto.password());
    }

    @Test
    void shouldRegisterRequestEqualsAndHashCode() {
        RegisterRequestDTO d1 = new RegisterRequestDTO(USER_ALICE, PASSWORD_PASS);
        RegisterRequestDTO d2 = new RegisterRequestDTO(USER_ALICE, PASSWORD_PASS);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldRegisterRequestNotBeEqualWhenDifferentFields() {
        RegisterRequestDTO d1 = new RegisterRequestDTO(USER_ALICE, null);
        RegisterRequestDTO d2 = new RegisterRequestDTO(USER_BOB, null);

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldRegisterRequestNotBeEqualToDifferentType() {
        RegisterRequestDTO dto = new RegisterRequestDTO(null, null);
        assertFalse(dto.equals(USER_OTHER));
    }

    @Test
    void shouldRegisterRequestToString() {
        RegisterRequestDTO dto = new RegisterRequestDTO(USER_BOB, null);
        String str = dto.toString();
        assertTrue(str.contains(USER_BOB));
    }

    @Test
    void shouldCreateAuthUserResponseDTO() {
        AuthUserResponseDTO dto = new AuthUserResponseDTO(USER_ALICE, CODE_MANAGER);
        assertNotNull(dto);
        assertEquals(USER_ALICE, dto.username());
        assertEquals(CODE_MANAGER, dto.role());
    }
}
