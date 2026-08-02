package com.credit.bladi.unittest.controller.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.controller.dto.user.request.CreateUserRequestDTO;
import com.credit.bladi.controller.dto.user.request.UpdateUserRoleRequestDTO;
import org.junit.jupiter.api.Test;

class UserDtoTest {

    @Test
    void shouldSetAndGetCreateUserRequestFields() {
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("newuser");
        dto.setPassword("securepass");
        dto.setRole("CLIENT");

        assertEquals("newuser", dto.getUsername());
        assertEquals("securepass", dto.getPassword());
        assertEquals("CLIENT", dto.getRole());
    }

    @Test
    void shouldCreateUserRequestEqualsAndHashCode() {
        CreateUserRequestDTO d1 = new CreateUserRequestDTO();
        d1.setUsername("alice");
        d1.setPassword("pass");
        d1.setRole("CLIENT");

        CreateUserRequestDTO d2 = new CreateUserRequestDTO();
        d2.setUsername("alice");
        d2.setPassword("pass");
        d2.setRole("CLIENT");

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldCreateUserRequestNotEqualWhenDifferentFields() {
        CreateUserRequestDTO d1 = new CreateUserRequestDTO();
        d1.setUsername("alice");

        CreateUserRequestDTO d2 = new CreateUserRequestDTO();
        d2.setUsername("bob");

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldCreateUserRequestNotEqualToDifferentType() {
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        assertFalse(dto.equals("other"));
    }

    @Test
    void shouldCreateUserRequestToString() {
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("alice");
        dto.setRole("CLIENT");
        String str = dto.toString();
        assertTrue(str.contains("alice"));
        assertTrue(str.contains("CLIENT"));
    }

    @Test
    void shouldSetAndGetUpdateUserRoleRequestField() {
        UpdateUserRoleRequestDTO dto = new UpdateUserRoleRequestDTO();
        dto.setRole("MANAGER");

        assertEquals("MANAGER", dto.getRole());
    }

    @Test
    void shouldUpdateUserRoleRequestEqualsAndHashCode() {
        UpdateUserRoleRequestDTO d1 = new UpdateUserRoleRequestDTO();
        d1.setRole("MANAGER");

        UpdateUserRoleRequestDTO d2 = new UpdateUserRoleRequestDTO();
        d2.setRole("MANAGER");

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldUpdateUserRoleRequestNotEqualWhenDifferentFields() {
        UpdateUserRoleRequestDTO d1 = new UpdateUserRoleRequestDTO();
        d1.setRole("MANAGER");

        UpdateUserRoleRequestDTO d2 = new UpdateUserRoleRequestDTO();
        d2.setRole("CLIENT");

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldUpdateUserRoleRequestNotEqualToDifferentType() {
        UpdateUserRoleRequestDTO dto = new UpdateUserRoleRequestDTO();
        assertFalse(dto.equals("other"));
    }

    @Test
    void shouldUpdateUserRoleRequestToString() {
        UpdateUserRoleRequestDTO dto = new UpdateUserRoleRequestDTO();
        dto.setRole("MANAGER");
        String str = dto.toString();
        assertTrue(str.contains("MANAGER"));
    }
}