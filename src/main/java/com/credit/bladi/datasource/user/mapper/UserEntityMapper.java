package com.credit.bladi.datasource.user.mapper;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.user.entity.UserEntity;
import com.credit.bladi.model.user.User;
import java.util.List;

public final class UserEntityMapper {

    private UserEntityMapper() {
    }

    public static CreditPage<User> mapToPage(List<UserEntity> entities, long total, int page, int size) {
        CreditPage<User> result = new CreditPage<>();
        result.setItems(entities.stream().map(UserEntityMapper::mapFromEntity).toList());
        result.setCurrentPage(page);
        result.setLastPage(size > 0 ? (int) Math.ceil((double) total / size) : 0);
        result.setSize(size);
        result.setTotal((int) total);
        return result;
    }

    public static User mapFromEntity(UserEntity userEntity) {
        return User.getBuilder()
            .id(userEntity.getId())
            .username(userEntity.getUsername())
            .password(userEntity.getPassword())
            .role(userEntity.getRole())
            .build();
    }

    public static UserEntity mapToNewEntity(User user) {
        if (user.getId() != null) {
            throw new IllegalArgumentException("mapToNewEntity does not support updates (user.id must be null)");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(user.getUsername());
        userEntity.setPassword(user.getPassword());
        userEntity.setRole(user.getRole());
        return userEntity;
    }
}