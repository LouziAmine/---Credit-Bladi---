package com.credit.bladi.repository.user;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.user.User;

public interface IUserAdminRepository extends IUserRepository {
    CreditPage<User> findAll(int page, int size);

    long countByRole(UserRole role);

    void updateRole(Long id, UserRole role);

    void updateProfile(Long id, Long gradeId, Long teamId, String email);
}
