package com.credit.bladi.unittest.mock.user;

import static com.credit.bladi.unittest.mock.data.MockUsers.PASSWORD_SECUREP_AT_SS1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JOHN_DOE;

import com.credit.bladi.controller.dto.user.request.CreateUserRequestDTO;
import com.credit.bladi.controller.dto.user.request.UpdateUserProfileRequestDTO;
import com.credit.bladi.controller.dto.user.request.UpdateUserRoleRequestDTO;
import com.credit.bladi.model.enums.UserRole;

public final class MockUser {
    public static final String USERNAME = USER_JOHN_DOE;
    public static final String PASSWORD = PASSWORD_SECUREP_AT_SS1;
    public static final UserRole ROLE = UserRole.CLIENT;

    private MockUser() {}

    public static CreateUserRequestDTO createRequest() {
        return new CreateUserRequestDTO(USERNAME, null, null, PASSWORD, ROLE.name(), null, null);
    }

    public static UpdateUserRoleRequestDTO updateRoleRequest() {
        return new UpdateUserRoleRequestDTO(UserRole.MANAGER.name());
    }

    public static UpdateUserProfileRequestDTO updateProfileRequest(String gradeCode, String teamCode) {
        return new UpdateUserProfileRequestDTO(gradeCode, teamCode, null);
    }
}
