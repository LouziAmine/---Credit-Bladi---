package com.credit.bladi.unittest.exception;

import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_9;
import static com.credit.bladi.unittest.mock.data.MockDates.DATE_2026_01_01;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CB_400;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDOE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_UNKNOWN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.exception.ApiException;
import com.credit.bladi.exception.ApiStatus;
import com.credit.bladi.exception.AuthException;
import com.credit.bladi.exception.AuthException.AuthExceptionType;
import com.credit.bladi.exception.BAMException;
import com.credit.bladi.exception.BAMException.BAMExceptionType;
import com.credit.bladi.exception.BamParametersException;
import com.credit.bladi.exception.BamParametersException.BamParametersExceptionType;
import com.credit.bladi.exception.CreditRateException;
import com.credit.bladi.exception.CreditRateException.CreditRateExceptionType;
import com.credit.bladi.exception.ErrorResponse;
import com.credit.bladi.exception.MortgageCreditException;
import com.credit.bladi.exception.MortgageCreditException.MortgageCreditExceptionType;
import com.credit.bladi.exception.MourabahaParametersException;
import com.credit.bladi.exception.MourabahaParametersException.MourabahaParametersExceptionType;
import com.credit.bladi.exception.ValidationException;
import com.credit.bladi.exception.ValidationException.ValidationExceptionType;
import org.junit.jupiter.api.Test;

class ExceptionsTest {
    @Test
    void shouldCreateAuthExceptionInvalidCredentials() {
        AuthException ex = AuthException.of(AuthExceptionType.INVALID_CREDENTIALS);
        assertEquals(AuthExceptionType.INVALID_CREDENTIALS, ex.getType());
        assertEquals(ApiStatus.UNAUTHORIZED, ex.getStatusCode());
        assertNotNull(ex.getMessage());
    }

    @Test
    void shouldCreateAuthExceptionUsernameTaken() {
        AuthException ex = AuthException.of(AuthExceptionType.USERNAME_TAKEN);
        assertEquals(AuthExceptionType.USERNAME_TAKEN, ex.getType());
        assertEquals(ApiStatus.CONFLICT, ex.getStatusCode());
        assertNotNull(ex.getMessage());
    }

    @Test
    void shouldCreateAuthExceptionUserNotFound() {
        AuthException ex = AuthException.of(AuthExceptionType.USER_NOT_FOUND);
        assertEquals(AuthExceptionType.USER_NOT_FOUND, ex.getType());
        assertEquals(ApiStatus.NOT_FOUND, ex.getStatusCode());
        assertNotNull(ex.getMessage());
    }

    @Test
    void shouldRejectDepositInsufficientWithoutRatioOverload() {
        assertThrows(IllegalArgumentException.class, () -> BAMException.of(BAMExceptionType.DEPOSIT_INSUFFICIENT));
    }

    @Test
    void shouldRejectDebtRatioExceededWithoutRatioOverload() {
        assertThrows(IllegalArgumentException.class, () -> BAMException.of(BAMExceptionType.DEBT_RATIO_EXCEEDED));
    }

    @Test
    void shouldCreateBAMExceptionDurationLimitExceeded() {
        BAMException ex = BAMException.of(BAMExceptionType.DURATION_LIMIT_EXCEEDED);
        assertEquals(BAMExceptionType.DURATION_LIMIT_EXCEEDED, ex.getType());
        assertEquals(ApiStatus.UNPROCESSABLE_CONTENT, ex.getStatusCode());
        assertNotNull(ex.getMessage());
    }

    @Test
    void shouldCreateBAMExceptionAgeLimitExceeded() {
        BAMException ex = BAMException.of(BAMExceptionType.AGE_LIMIT_EXCEEDED);
        assertEquals(BAMExceptionType.AGE_LIMIT_EXCEEDED, ex.getType());
        assertNotNull(ex.getMessage());
    }

    @Test
    void shouldRejectDurationLimitExceededWithRatioOverload() {
        assertThrows(IllegalArgumentException.class,
            () -> BAMException.of(BAMExceptionType.DURATION_LIMIT_EXCEEDED, DECIMAL_0_9));
    }

    @Test
    void shouldRejectAgeLimitExceededWithRatioOverload() {
        assertThrows(IllegalArgumentException.class,
            () -> BAMException.of(BAMExceptionType.AGE_LIMIT_EXCEEDED, DECIMAL_0_9));
    }

    @Test
    void shouldCreateSimulationExceptionCspNotFound() {
        MortgageCreditException ex = MortgageCreditException.of(MortgageCreditExceptionType.CSP_NOT_FOUND);
        assertEquals(MortgageCreditExceptionType.CSP_NOT_FOUND, ex.getType());
        assertEquals(ApiStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void shouldCreateSimulationExceptionStrategyNotFound() {
        MortgageCreditException ex = MortgageCreditException.of(MortgageCreditExceptionType.STRATEGY_NOT_FOUND);
        assertEquals(MortgageCreditExceptionType.STRATEGY_NOT_FOUND, ex.getType());
        assertEquals(ApiStatus.INTERNAL_SERVER_ERROR, ex.getStatusCode());
        assertNotNull(ex.getMessage());
    }

    @Test
    void shouldCreateSimulationExceptionBamParametersNotFound() {
        MortgageCreditException ex = MortgageCreditException.of(MortgageCreditExceptionType.BAM_PARAMETERS_NOT_FOUND);
        assertEquals(MortgageCreditExceptionType.BAM_PARAMETERS_NOT_FOUND, ex.getType());
        assertEquals(ApiStatus.SERVICE_UNAVAILABLE, ex.getStatusCode());
        assertNotNull(ex.getMessage());
    }

    @Test
    void shouldCreateSimulationExceptionMourabahaParametersNotFound() {
        MortgageCreditException ex = MortgageCreditException
            .of(MortgageCreditExceptionType.MOURABAHA_PARAMETERS_NOT_FOUND);
        assertEquals(MortgageCreditExceptionType.MOURABAHA_PARAMETERS_NOT_FOUND, ex.getType());
        assertEquals(ApiStatus.SERVICE_UNAVAILABLE, ex.getStatusCode());
        assertNotNull(ex.getMessage());
    }

    @Test
    void shouldCreateBamParametersExceptionNotFound() {
        BamParametersException ex = BamParametersException.of(BamParametersExceptionType.NOT_FOUND);
        assertEquals(BamParametersExceptionType.NOT_FOUND, ex.getType());
        assertEquals(ApiStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void shouldCreateBamParametersExceptionInvalidProcessingFeesRange() {
        BamParametersException ex = BamParametersException.of(BamParametersExceptionType.INVALID_PROCESSING_FEES_RANGE);
        assertEquals(BamParametersExceptionType.INVALID_PROCESSING_FEES_RANGE, ex.getType());
        assertEquals(ApiStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("bamParameters.invalidProcessingFeesRange", ex.getMessageKey());
    }

    @Test
    void shouldCreateMourabahaParametersExceptionNotFound() {
        MourabahaParametersException ex =
            MourabahaParametersException.of(MourabahaParametersExceptionType.NOT_FOUND);
        assertEquals(MourabahaParametersExceptionType.NOT_FOUND, ex.getType());
        assertEquals(ApiStatus.NOT_FOUND, ex.getStatusCode());
        assertNotNull(ex.getMessage());
    }

    @Test
    void shouldCreateCreditRateExceptionNotFound() {
        CreditRateException ex = CreditRateException.of(CreditRateExceptionType.NOT_FOUND);
        assertEquals(CreditRateExceptionType.NOT_FOUND, ex.getType());
        assertEquals(ApiStatus.NOT_FOUND, ex.getStatusCode());
        assertNotNull(ex.getMessage());
    }

    @Test
    void shouldCreateCreditRateExceptionInvalidDurationRange() {
        CreditRateException ex = CreditRateException.of(CreditRateExceptionType.INVALID_DURATION_RANGE);
        assertEquals(CreditRateExceptionType.INVALID_DURATION_RANGE, ex.getType());
        assertEquals(ApiStatus.BAD_REQUEST, ex.getStatusCode());
        assertNotNull(ex.getMessage());
    }

    @Test
    void shouldCreateAuthExceptionSelfRoleModificationForbidden() {
        AuthException ex = AuthException.of(AuthExceptionType.SELF_ROLE_MODIFICATION_FORBIDDEN);
        assertEquals(ApiStatus.FORBIDDEN, ex.getStatusCode());
        assertNotNull(ex.getMessage());
    }

    @Test
    void shouldCreateAuthExceptionLastManagerDemotionForbidden() {
        AuthException ex = AuthException.of(AuthExceptionType.LAST_MANAGER_DEMOTION_FORBIDDEN);
        assertEquals(ApiStatus.FORBIDDEN, ex.getStatusCode());
        assertNotNull(ex.getMessage());
    }

    @Test
    void shouldCreateAuthExceptionInvalidRefreshToken() {
        AuthException ex = AuthException.of(AuthExceptionType.INVALID_REFRESH_TOKEN);
        assertEquals(ApiStatus.UNAUTHORIZED, ex.getStatusCode());
        assertNotNull(ex.getMessage());
    }

    @Test
    void shouldCreateAuthExceptionRefreshTokenExpired() {
        AuthException ex = AuthException.of(AuthExceptionType.REFRESH_TOKEN_EXPIRED);
        assertEquals(ApiStatus.UNAUTHORIZED, ex.getStatusCode());
        assertNotNull(ex.getMessage());
    }

    @Test
    void shouldCreateAuthExceptionRefreshTokenReuseDetected() {
        AuthException ex = AuthException.of(AuthExceptionType.REFRESH_TOKEN_REUSE_DETECTED);
        assertEquals(ApiStatus.UNAUTHORIZED, ex.getStatusCode());
        assertNotNull(ex.getMessage());
    }

    @Test
    void shouldCreateAuthExceptionAccountLocked() {
        AuthException ex = AuthException.of(AuthExceptionType.ACCOUNT_LOCKED);
        assertEquals(ApiStatus.LOCKED, ex.getStatusCode());
        assertNotNull(ex.getMessage());
    }

    @Test
    void shouldCreateAuthExceptionNoGrantedAuthorityWithoutDetail() {
        AuthException ex = AuthException.of(AuthExceptionType.NO_GRANTED_AUTHORITY);
        assertEquals(ApiStatus.INTERNAL_SERVER_ERROR, ex.getStatusCode());
        assertTrue(ex.getMessage().contains(USER_UNKNOWN));
    }

    @Test
    void shouldCreateAuthExceptionNoGrantedAuthorityWithDetail() {
        AuthException ex = AuthException.of(AuthExceptionType.NO_GRANTED_AUTHORITY, USER_JDOE);
        assertEquals(ApiStatus.INTERNAL_SERVER_ERROR, ex.getStatusCode());
        assertTrue(ex.getMessage().contains(USER_JDOE));
    }

    @Test
    void shouldCreateValidationExceptionBatchNotAllowed() {
        ValidationException ex = ValidationException.of(ValidationExceptionType.BATCH_NOT_ALLOWED);
        assertEquals(ValidationExceptionType.BATCH_NOT_ALLOWED, ex.getType());
        assertEquals(ApiStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("validation.batchNotAllowed", ex.getMessageKey());
    }

    @Test
    void shouldCreateValidationExceptionUnknownKeyWithDetail() {
        ValidationException ex = ValidationException.of(ValidationExceptionType.UNKNOWN_KEY, "someField");
        assertEquals(ValidationExceptionType.UNKNOWN_KEY, ex.getType());
        assertTrue(ex.getMessage().contains("someField"));
    }

    @Test
    void shouldCreateValidationExceptionInvalidValueWithDetail() {
        ValidationException ex = ValidationException.of(ValidationExceptionType.INVALID_VALUE, "cspCode: NOPE");
        assertTrue(ex.getMessage().contains("cspCode: NOPE"));
    }

    @Test
    void shouldCreateValidationExceptionInvalidValueWithoutDetail() {
        ValidationException ex = ValidationException.of(ValidationExceptionType.INVALID_VALUE, null);
        assertEquals("validation.invalidValue", ex.getMessageKey());
    }

    @Test
    void shouldCreateValidationExceptionUnknownKeyWithoutDetail() {
        ValidationException ex = ValidationException.of(ValidationExceptionType.UNKNOWN_KEY, null);
        assertEquals(ValidationExceptionType.UNKNOWN_KEY, ex.getType());
        assertNotNull(ex.getMessage());
    }

    @Test
    void shouldCreateValidationExceptionInvalidValue() {
        ValidationException ex = ValidationException.of(ValidationExceptionType.INVALID_VALUE, "page");
        assertEquals(ValidationExceptionType.INVALID_VALUE, ex.getType());
        assertEquals(ApiStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void shouldCreateErrorResponse() {
        ErrorResponse response = new ErrorResponse("req1", "2026-01-01 00:00:00", LABEL_CB_400, "error desc");
        assertEquals("req1", response.requestId());
        assertEquals("2026-01-01 00:00:00", response.timestamp());
        assertEquals(LABEL_CB_400, response.errorCode());
        assertEquals("error desc", response.description());
    }

    @Test
    void shouldErrorResponseToStringIncludeAllFields() {
        ErrorResponse response = new ErrorResponse("req1", DATE_2026_01_01, LABEL_CB_400, "desc");
        String str = response.toString();
        assertTrue(str.contains("req1"));
        assertTrue(str.contains(LABEL_CB_400));
        assertTrue(str.contains("desc"));
    }

    @Test
    void shouldCreateApiException() {
        ApiException ex = new ApiException(ApiStatus.SERVICE_UNAVAILABLE, "test.dummy");
        assertEquals(ApiStatus.SERVICE_UNAVAILABLE, ex.getStatusCode());
    }
}
