package com.credit.bladi.service.auth;

public interface ITokenMaintenanceService {
    void purgeExpiredTokens();
}
