package com.credit.bladi.controller.user.mapper;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.common.CreditPageMapper;
import com.credit.bladi.controller.dto.user.request.CreateUserRequestDTO;
import com.credit.bladi.controller.dto.user.response.UserResponseDTO;
import com.credit.bladi.exception.ValidationException;
import com.credit.bladi.exception.ValidationException.ValidationExceptionType;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.user.User;

public final class UserControllerMapper {

    private UserControllerMapper() {
    }

    public static UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getRole().name());
    }

    public static CreditPage<UserResponseDTO> toCreditPage(CreditPage<User> source) {
        return CreditPageMapper.map(source, UserControllerMapper::toResponseDTO);
    }

    public static User toUser(CreateUserRequestDTO createUserRequest) {
        return User.getBuilder()
            .username(createUserRequest.getUsername())
            .password(createUserRequest.getPassword())
            .role(toRole(createUserRequest.getRole()))
            .build();
    }

    public static UserRole toRole(String role) {
        try {
            return UserRole.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw ValidationException.of(ValidationExceptionType.INVALID_VALUE, "role: " + role);
        }
    }
}