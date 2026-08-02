package com.credit.bladi.unittest.mock.user;

import com.credit.bladi.controller.dto.user.request.CreateUserRequestDTO;
import com.credit.bladi.controller.dto.user.request.UpdateUserRoleRequestDTO;
import com.credit.bladi.model.enums.UserRole;

public final class MockUser {

    public static final String USERNAME = "john.doe";
    public static final String PASSWORD = "secureP@ss1";
    public static final UserRole ROLE = UserRole.CLIENT;

    private MockUser() {}

    public static CreateUserRequestDTO createRequest() {
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername(USERNAME);
        dto.setPassword(PASSWORD);
        dto.setRole(ROLE.name());
        return dto;
    }

    public static UpdateUserRoleRequestDTO updateRoleRequest() {
        UpdateUserRoleRequestDTO dto = new UpdateUserRoleRequestDTO();
        dto.setRole(UserRole.MANAGER.name());
        return dto;
    }
}