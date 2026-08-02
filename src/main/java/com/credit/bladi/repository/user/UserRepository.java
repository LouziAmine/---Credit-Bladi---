package com.credit.bladi.repository.user;

import static com.credit.bladi.exception.AuthException.AuthExceptionType.USER_NOT_FOUND;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.user.UserDatasource;
import com.credit.bladi.datasource.user.entity.UserEntity;
import com.credit.bladi.datasource.user.mapper.UserEntityMapper;
import com.credit.bladi.exception.AuthException;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.user.User;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository implements IUserRepository {

    private static final Logger LOGGER = LogManager.getLogger(UserRepository.class);

    private final UserDatasource datasource;

    public UserRepository(UserDatasource datasource) {
        this.datasource = datasource;
    }

    @Override
    public CreditPage<User> findAll(int page, int size) {
        LOGGER.debug("findAll - page: {}, size: {}", page, size);
        Page<UserEntity> result = datasource.findAll(PageRequest.of(page - 1, size, Sort.by("id")));
        return UserEntityMapper.mapToPage(result.getContent(), result.getTotalElements(), page, size);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        LOGGER.debug("findByUsername - username: {}", username);
        return datasource.findByUsername(username).map(UserEntityMapper::mapFromEntity);
    }

    @Override
    public Optional<User> findById(Long id) {
        LOGGER.debug("findById - id: {}", id);
        return datasource.findById(id).map(UserEntityMapper::mapFromEntity);
    }

    @Override
    public boolean existsByUsername(String username) {
        return datasource.existsByUsername(username);
    }

    @Override
    public long countByRole(UserRole role) {
        return datasource.countByRole(role);
    }

    @Override
    public User save(User user) {
        User saved = UserEntityMapper.mapFromEntity(datasource.save(UserEntityMapper.mapToNewEntity(user)));
        LOGGER.debug("save - username: {}, role: {}", user.getUsername(), user.getRole());
        return saved;
    }

    @Override
    public void updateRole(Long id, UserRole role) {
        int updated = datasource.updateRole(id, role);
        if (updated == 0) {
            throw AuthException.of(USER_NOT_FOUND);
        }
        LOGGER.debug("updateRole - userId: {}, role: {}", id, role);
    }
}