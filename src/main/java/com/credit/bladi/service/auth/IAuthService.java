package com.credit.bladi.service.auth;

import com.credit.bladi.model.auth.AuthLoginResult;
import com.credit.bladi.model.user.User;

public interface IAuthService {
    AuthLoginResult login(User credentials, String clientIp);

    void register(User user);

    AuthLoginResult refresh(String refreshToken);

    void logout(String accessToken, String refreshToken);
}
