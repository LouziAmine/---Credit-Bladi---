package com.credit.bladi.repository.user;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.user.User;
import java.util.Optional;

public interface IUserRepository {

    CreditPage<User> findAll(int page, int size);

    Optional<User> findByUsername(String username);

    Optional<User> findById(Long id);

    boolean existsByUsername(String username);

    long countByRole(UserRole role);

    User save(User user);

    void updateRole(Long id, UserRole role);
}