package com.credit.bladi.controller.user.mapper;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.common.CreditPageMapper;
import com.credit.bladi.common.EnumParsers;
import com.credit.bladi.controller.dto.user.request.CreateUserRequestDTO;
import com.credit.bladi.controller.dto.user.response.UserResponseDTO;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.user.User;

public final class UserControllerMapper {
    private UserControllerMapper() {
    }

    public static UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(user.getPublicId(), user.getUsername(), user.getFullName(), user.getEmail(),
            user.getRole().name(), user.getGradeCode(), user.getGradeName(), user.getTeamCode(),
            user.getTeamName());
    }

    public static CreditPage<UserResponseDTO> toCreditPage(CreditPage<User> source) {
        return CreditPageMapper.map(source, UserControllerMapper::toResponseDTO);
    }

    public static User toUser(CreateUserRequestDTO createUserRequest) {
        return User.getBuilder()
            .username(createUserRequest.username())
            .fullName(createUserRequest.fullName())
            .email(createUserRequest.email())
            .password(createUserRequest.password())
            .role(toRole(createUserRequest.role()))
            .build();
    }

    public static UserRole toRole(String role) {
        return EnumParsers.parse(UserRole.class, role, "role");
    }
}
