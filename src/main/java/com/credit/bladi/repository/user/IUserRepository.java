package com.credit.bladi.repository.user;

import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.user.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserRepository {
    Optional<User> findByUsername(String username);

    Optional<User> findById(Long id);

    List<User> findAllById(List<Long> ids);

    Optional<Long> findIdByPublicId(UUID publicId);

    boolean existsByUsername(String username);

    List<User> findManagersByGradeIdAndTeamId(Long gradeId, Long teamId);

    List<User> findManagersByTeamId(Long teamId);

    List<User> findAllByRole(UserRole role);

    List<User> findClientsByEmail(String email);

    User save(User user);
}
