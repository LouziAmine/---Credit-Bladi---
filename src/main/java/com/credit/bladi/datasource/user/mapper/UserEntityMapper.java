package com.credit.bladi.datasource.user.mapper;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.user.entity.UserEntity;
import com.credit.bladi.model.user.User;
import java.util.List;

public final class UserEntityMapper {
    private UserEntityMapper() {
    }

    public static CreditPage<User> mapToPage(List<UserEntity> entities, long total, int page, int size) {
        return CreditPage.of(entities.stream().map(UserEntityMapper::mapFromEntity).toList(), page, size, total);
    }

    public static User mapFromEntity(UserEntity userEntity) {
        return User.getBuilder()
            .id(userEntity.getId())
            .publicId(userEntity.getPublicId())
            .username(userEntity.getUsername())
            .fullName(userEntity.getFullName())
            .email(userEntity.getEmail())
            .password(userEntity.getPassword())
            .role(userEntity.getRole())
            .gradeId(userEntity.getGradeId())
            .teamId(userEntity.getTeamId())
            .preferredLanguage(userEntity.getPreferredLanguage())
            .build();
    }

    public static UserEntity mapToNewEntity(User user) {
        if (user.getId() != null) {
            throw new IllegalArgumentException("mapToNewEntity does not support updates (user.id must be null)");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(user.getUsername());
        userEntity.setFullName(user.getFullName());
        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(user.getPassword());
        userEntity.setRole(user.getRole());
        userEntity.setGradeId(user.getGradeId());
        userEntity.setTeamId(user.getTeamId());
        userEntity.setPreferredLanguage(user.getPreferredLanguage());
        return userEntity;
    }
}
