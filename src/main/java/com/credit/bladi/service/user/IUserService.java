package com.credit.bladi.service.user;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.user.User;
import java.util.Optional;

public interface IUserService {

    CreditPage<User> getAllUsers(int page, int size);

    Long createUser(User user);

    void updateUserRole(Long id, UserRole role, String currentUsername);

    Optional<User> findByUsername(String username);

    User getUserById(Long id);
}