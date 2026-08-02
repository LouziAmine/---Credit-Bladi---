package com.credit.bladi.unittest.controller.dto;

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
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsername("johndoe");
        dto.setPassword("secret");

        assertEquals("johndoe", dto.getUsername());
        assertEquals("secret", dto.getPassword());
    }

    @Test
    void shouldLoginRequestEqualsAndHashCode() {
        LoginRequestDTO d1 = new LoginRequestDTO();
        d1.setUsername("alice");
        d1.setPassword("pass");

        LoginRequestDTO d2 = new LoginRequestDTO();
        d2.setUsername("alice");
        d2.setPassword("pass");

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldLoginRequestNotBeEqualWhenDifferentFields() {
        LoginRequestDTO d1 = new LoginRequestDTO();
        d1.setUsername("alice");

        LoginRequestDTO d2 = new LoginRequestDTO();
        d2.setUsername("bob");

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldLoginRequestNotBeEqualToDifferentType() {
        LoginRequestDTO dto = new LoginRequestDTO();
        assertFalse(dto.equals("other"));
    }

    @Test
    void shouldLoginRequestToString() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsername("alice");
        String str = dto.toString();
        assertTrue(str.contains("alice"));
    }

    @Test
    void shouldSetAndGetRegisterRequestFields() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setUsername("newuser");
        dto.setPassword("newpass");

        assertEquals("newuser", dto.getUsername());
        assertEquals("newpass", dto.getPassword());
    }

    @Test
    void shouldRegisterRequestEqualsAndHashCode() {
        RegisterRequestDTO d1 = new RegisterRequestDTO();
        d1.setUsername("alice");
        d1.setPassword("pass");

        RegisterRequestDTO d2 = new RegisterRequestDTO();
        d2.setUsername("alice");
        d2.setPassword("pass");

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldRegisterRequestNotBeEqualWhenDifferentFields() {
        RegisterRequestDTO d1 = new RegisterRequestDTO();
        d1.setUsername("alice");

        RegisterRequestDTO d2 = new RegisterRequestDTO();
        d2.setUsername("bob");

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldRegisterRequestNotBeEqualToDifferentType() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        assertFalse(dto.equals("other"));
    }

    @Test
    void shouldRegisterRequestToString() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setUsername("bob");
        String str = dto.toString();
        assertTrue(str.contains("bob"));
    }

    @Test
    void shouldCreateAuthUserResponseDTO() {
        AuthUserResponseDTO dto = new AuthUserResponseDTO("alice", "MANAGER");
        assertNotNull(dto);
        assertEquals("alice", dto.username());
        assertEquals("MANAGER", dto.role());
    }
}