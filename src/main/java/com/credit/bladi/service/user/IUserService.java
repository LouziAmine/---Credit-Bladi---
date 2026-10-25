package com.credit.bladi.service.user;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.user.User;
import java.util.Optional;
import java.util.UUID;

public interface IUserService {
    CreditPage<User> getAllUsers(int page, int size);

    UUID createUser(User user, String gradeCode, String teamCode);

    Long resolveUserId(UUID publicId);

    void updateUserRole(Long id, UserRole role, String currentUsername);

    void updateUserProfile(Long id, String gradeCode, String teamCode, String email, String currentUsername);

    Optional<User> findByUsername(String username);

    User getUserById(Long id);
}
