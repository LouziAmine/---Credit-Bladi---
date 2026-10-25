package com.credit.bladi.unittest.exception.handler;

import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CB_400;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CB_401;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CB_403;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_UTF_8;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_UNKNOWN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.credit.bladi.config.web.RequestContext;
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
import com.credit.bladi.exception.handler.ApiExceptionHandler;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.lang.reflect.Method;
import java.time.Clock;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

@ExtendWith(MockitoExtension.class)
class ApiExceptionHandlerTest {
    @Mock
    private RequestContext requestContext;

    private ApiExceptionHandler handler;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(
            "i18n/common/identity", "i18n/common/error", "i18n/common/security", "i18n/common/path-params",
            "i18n/exception/auth", "i18n/exception/bam", "i18n/exception/activity-sector",
            "i18n/exception/bam-parameters", "i18n/exception/credit-rate", "i18n/exception/investment-credit",
            "i18n/exception/enterprise", "i18n/exception/mourabaha-parameters", "i18n/exception/mortgage-credit",
            "i18n/exception/validation", "i18n/exception/workflow", "i18n/validator/iso-country-code",
            "i18n/controller/enterprise", "i18n/dto/login-request", "i18n/dto/create-activity-sector-request",
            "i18n/dto/create-enterprise-request", "i18n/dto/add-demand-comment-request",
            "i18n/dto/classify-demand-request", "i18n/dto/advance-demand-request", "i18n/dto/patch-bam-parameters",
            "i18n/dto/patch-mourabaha-parameters", "i18n/dto/patch-credit-rate",
            "i18n/dto/patch-investment-credit-parameters", "i18n/dto/investment-credit-request",
            "i18n/dto/mortgage-credit-request");
        messageSource.setDefaultEncoding(LABEL_UTF_8);
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        handler = new ApiExceptionHandler(requestContext, messageSource, Clock.systemUTC());
        when(requestContext.getRequestId()).thenReturn("test-req-id");
    }

    @AfterEach
    void tearDown() {
        LocaleContextHolder.resetLocaleContext();
    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<ErrorResponse> invokeProtected(String methodName, Class<?> paramType, Object arg)
            throws Exception {
        Method method = ApiExceptionHandler.class.getDeclaredMethod(methodName, paramType);
        method.setAccessible(true);
        return (ResponseEntity<ErrorResponse>) method.invoke(handler, arg);
    }

    @Test
    void shouldHandleMethodArgumentNotValidWithFieldErrors() throws Exception {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("obj", "amount", "Amount is required");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponse> result = invokeProtected(
            "handleMethodArgumentNotValid", MethodArgumentNotValidException.class, ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(LABEL_CB_400, result.getBody().errorCode());
    }

    @Test
    void shouldHandleMethodArgumentNotValidWithNoFieldErrors() throws Exception {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "obj");
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> result = invokeProtected(
            "handleMethodArgumentNotValid", MethodArgumentNotValidException.class, ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void shouldHandleMethodArgumentNotValidWithBindingFailure() throws Exception {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn("amount");
        when(fieldError.isBindingFailure()).thenReturn(true);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponse> result = invokeProtected(
            "handleMethodArgumentNotValid", MethodArgumentNotValidException.class, ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void shouldHandleMethodArgumentNotValidWithMultipleFieldErrors() throws Exception {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError field1 = new FieldError("obj", "amount", "required");
        FieldError field2 = new FieldError("obj", "duration", "required");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(field1, field2));

        ResponseEntity<ErrorResponse> result = invokeProtected(
            "handleMethodArgumentNotValid", MethodArgumentNotValidException.class, ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void shouldHandleHttpMediaTypeNotSupported() {
        HttpMediaTypeNotSupportedException ex = mock(HttpMediaTypeNotSupportedException.class);

        ResponseEntity<ErrorResponse> result = handler.handleHttpMediaTypeNotSupported(ex);

        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("CB-415", result.getBody().errorCode());
    }

    @Test
    void shouldHandleDataIntegrityViolation() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("unique constraint violated");

        ResponseEntity<ErrorResponse> result = handler.handleDataIntegrityViolation(ex);

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("CB-409", result.getBody().errorCode());
    }

    @Test
    void shouldHandleAccessDenied() {
        AccessDeniedException ex = new AccessDeniedException("denied");

        ResponseEntity<ErrorResponse> result = handler.handleAccessDenied(ex);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(LABEL_CB_403, result.getBody().errorCode());
    }

    @Test
    void shouldHandleHttpMediaTypeNotAcceptable() {
        HttpMediaTypeNotAcceptableException ex = mock(HttpMediaTypeNotAcceptableException.class);

        ResponseEntity<ErrorResponse> result = handler.handleHttpMediaTypeNotAcceptable(ex);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("CB-406", result.getBody().errorCode());
    }

    @Test
    void shouldHandleHttpMessageNotReadableWithUnrecognizedProperty() throws Exception {
        UnrecognizedPropertyException cause = mock(UnrecognizedPropertyException.class);
        when(cause.getPropertyName()).thenReturn("unknownField");
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getCause()).thenReturn(cause);

        ResponseEntity<ErrorResponse> result = invokeProtected(
            "handleHttpMessageNotReadable", HttpMessageNotReadableException.class, ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void shouldHandleHttpMessageNotReadableWithGenericCause() throws Exception {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getCause()).thenReturn(new RuntimeException("generic error"));

        ResponseEntity<ErrorResponse> result = invokeProtected(
            "handleHttpMessageNotReadable", HttpMessageNotReadableException.class, ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void shouldHandleHttpMessageNotReadableWithNullCause() throws Exception {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getCause()).thenReturn(null);

        ResponseEntity<ErrorResponse> result = invokeProtected(
            "handleHttpMessageNotReadable", HttpMessageNotReadableException.class, ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void shouldHandleAuthException() {
        AuthException ex = AuthException.of(AuthExceptionType.INVALID_CREDENTIALS);

        ResponseEntity<ErrorResponse> result = handler.handleApiException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(LABEL_CB_401, result.getBody().errorCode());
    }

    @Test
    void shouldHandleBAMException() {
        BAMException ex = BAMException.of(BAMExceptionType.DURATION_LIMIT_EXCEEDED);

        ResponseEntity<ErrorResponse> result = handler.handleApiException(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void shouldHandleSimulationException() {
        MortgageCreditException ex = MortgageCreditException.of(MortgageCreditExceptionType.CSP_NOT_FOUND);

        ResponseEntity<ErrorResponse> result = handler.handleApiException(ex);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void shouldHandleBamParametersException() {
        BamParametersException ex = BamParametersException.of(BamParametersExceptionType.NOT_FOUND);

        ResponseEntity<ErrorResponse> result = handler.handleApiException(ex);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void shouldHandleCreditRateException() {
        CreditRateException ex = CreditRateException.of(CreditRateExceptionType.NOT_FOUND);

        ResponseEntity<ErrorResponse> result = handler.handleApiException(ex);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void shouldHandleMourabahaParametersException() {
        MourabahaParametersException ex =
            MourabahaParametersException.of(MourabahaParametersExceptionType.NOT_FOUND);

        ResponseEntity<ErrorResponse> result = handler.handleApiException(ex);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Mourabaha parameters record not found for the given ID.", result.getBody().description());
    }

    @Test
    void shouldHandleMethodNotSupported() {
        HttpRequestMethodNotSupportedException ex = mock(HttpRequestMethodNotSupportedException.class);

        ResponseEntity<ErrorResponse> result = handler.handleMethodNotSupported(ex);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("CB-405", result.getBody().errorCode());
    }

    @Test
    void shouldHandleMethodArgumentTypeMismatch() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("page");

        ResponseEntity<ErrorResponse> result = handler.handleMethodArgumentTypeMismatch(ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void shouldHandleOptimisticLockingFailure() {
        OptimisticLockingFailureException ex = new OptimisticLockingFailureException("stale entity");

        ResponseEntity<ErrorResponse> result = handler.handleOptimisticLockingFailure(ex);

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("bad input");

        ResponseEntity<ErrorResponse> result = handler.handleIllegalArgumentException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertNotEquals("bad input", result.getBody().description(), "internal exception text must not leak");
    }

    @Test
    void shouldHandleMismatchedInputExceptionWithNullMessageAndNonArrayType() throws Exception {
        MismatchedInputException ex = mock(MismatchedInputException.class);
        when(ex.getTargetType()).thenReturn((Class) String.class);
        when(ex.getMessage()).thenReturn(null);
        when(ex.getPath()).thenReturn(List.of());

        ResponseEntity<ErrorResponse> result = invokeProtected(
            "handleMismatchedInputException", MismatchedInputException.class, ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldHandleMismatchedInputExceptionWithLongPathSkippingToLastTwoElements() throws Exception {
        MismatchedInputException ex = mock(MismatchedInputException.class);
        when(ex.getTargetType()).thenReturn((Class) String.class);
        when(ex.getMessage()).thenReturn("type mismatch");
        tools.jackson.databind.exc.MismatchedInputException.Reference ref1 =
            mock(tools.jackson.databind.exc.MismatchedInputException.Reference.class);
        tools.jackson.databind.exc.MismatchedInputException.Reference ref2 =
            mock(tools.jackson.databind.exc.MismatchedInputException.Reference.class);
        when(ref2.getPropertyName()).thenReturn("inner");
        tools.jackson.databind.exc.MismatchedInputException.Reference ref3 =
            mock(tools.jackson.databind.exc.MismatchedInputException.Reference.class);
        when(ref3.getPropertyName()).thenReturn("amount");
        when(ex.getPath()).thenReturn(java.util.List.of(ref1, ref2, ref3));

        ResponseEntity<ErrorResponse> result = invokeProtected(
            "handleMismatchedInputException", MismatchedInputException.class, ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().description().contains("inner.amount"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldHandleMismatchedInputExceptionWithIndexedPathElement() throws Exception {
        MismatchedInputException ex = mock(MismatchedInputException.class);
        when(ex.getTargetType()).thenReturn((Class) String.class);
        when(ex.getMessage()).thenReturn("type mismatch");
        tools.jackson.databind.exc.MismatchedInputException.Reference ref =
            mock(tools.jackson.databind.exc.MismatchedInputException.Reference.class);
        when(ref.getPropertyName()).thenReturn(null);
        when(ref.getIndex()).thenReturn(2);
        when(ex.getPath()).thenReturn(java.util.List.of(ref));

        ResponseEntity<ErrorResponse> result = invokeProtected(
            "handleMismatchedInputException", MismatchedInputException.class, ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().description().contains("[2]"));
    }

    @Test
    void shouldHandleMismatchedInputExceptionWithEmptyPath() throws Exception {
        MismatchedInputException ex = mock(MismatchedInputException.class);
        when(ex.getTargetType()).thenReturn((Class) String.class);
        when(ex.getMessage()).thenReturn("type mismatch");
        when(ex.getPath()).thenReturn(List.of());

        ResponseEntity<ErrorResponse> result = invokeProtected(
            "handleMismatchedInputException", MismatchedInputException.class, ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().description().contains(USER_UNKNOWN));
    }

    @Test
    void shouldHandleMissingServletRequestParameter() {
        MissingServletRequestParameterException ex =
            new MissingServletRequestParameterException("page", "Integer");

        ResponseEntity<ErrorResponse> result = handler.handleMissingServletRequestParameter(ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void shouldHandleNoResourceFound() {
        NoResourceFoundException ex = mock(NoResourceFoundException.class);

        ResponseEntity<ErrorResponse> result = handler.handleNoResourceFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void shouldHandleConstraintViolationException() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("must not be null");
        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<ErrorResponse> result = handler.handleConstraintViolationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("must not be null", result.getBody().description());
    }

    @Test
    void shouldHandleConstraintViolationExceptionWithNoViolations() {
        ConstraintViolationException ex = new ConstraintViolationException(Set.of());

        ResponseEntity<ErrorResponse> result = handler.handleConstraintViolationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void shouldHandleValidationException() {
        ValidationException ex = ValidationException.of(ValidationExceptionType.INVALID_VALUE, "page");

        ResponseEntity<ErrorResponse> result = handler.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void shouldHandleUnexpectedException() {
        RuntimeException ex = new RuntimeException("unexpected");

        ResponseEntity<ErrorResponse> result = handler.handleUnexpectedException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("CB-500", result.getBody().errorCode());
    }

    @Test
    void shouldHandleMismatchedInputExceptionDirectly() throws Exception {
        MismatchedInputException ex = mock(MismatchedInputException.class);
        when(ex.getTargetType()).thenReturn((Class) String[].class);

        ResponseEntity<ErrorResponse> result = invokeProtected(
            "handleMismatchedInputException", MismatchedInputException.class, ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void shouldHandleHttpMessageNotReadableWithMismatchedInput() throws Exception {
        MismatchedInputException cause = mock(MismatchedInputException.class);
        when(cause.getTargetType()).thenReturn((Class) String[].class);
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getCause()).thenReturn(cause);

        ResponseEntity<ErrorResponse> result = invokeProtected(
            "handleHttpMessageNotReadable", HttpMessageNotReadableException.class, ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void shouldHandleMismatchedInputExceptionWithTrailingToken() throws Exception {
        MismatchedInputException ex = mock(MismatchedInputException.class);
        when(ex.getTargetType()).thenReturn(null);
        when(ex.getMessage()).thenReturn("Trailing token found");

        ResponseEntity<ErrorResponse> result = invokeProtected(
            "handleMismatchedInputException", MismatchedInputException.class, ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldHandleMismatchedInputExceptionWithPathExtraction() throws Exception {
        MismatchedInputException ex = mock(MismatchedInputException.class);
        when(ex.getTargetType()).thenReturn((Class) String.class);
        when(ex.getMessage()).thenReturn("type mismatch");
        tools.jackson.databind.exc.MismatchedInputException.Reference ref =
            mock(tools.jackson.databind.exc.MismatchedInputException.Reference.class);
        when(ref.getPropertyName()).thenReturn("amount");
        when(ex.getPath()).thenReturn(java.util.List.of(ref));

        ResponseEntity<ErrorResponse> result = invokeProtected(
            "handleMismatchedInputException", MismatchedInputException.class, ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void shouldHandleApiException() {
        com.credit.bladi.exception.ApiException ex = new com.credit.bladi.exception.ApiException(
            com.credit.bladi.exception.ApiStatus.SERVICE_UNAVAILABLE, "error.status.default");

        ResponseEntity<ErrorResponse> result = handler.handleApiException(ex);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void shouldHandleApiExceptionWith404StatusCode() {
        when(requestContext.getRequestId()).thenReturn(null);
        com.credit.bladi.exception.ApiException ex = new com.credit.bladi.exception.ApiException(
            com.credit.bladi.exception.ApiStatus.NOT_FOUND, "error.status.default");

        ResponseEntity<ErrorResponse> result = handler.handleApiException(ex);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNotNull(result.getBody().requestId());
    }

    @Test
    void shouldHandleApiExceptionWith401StatusCode() {
        com.credit.bladi.exception.ApiException ex = new com.credit.bladi.exception.ApiException(
            com.credit.bladi.exception.ApiStatus.UNAUTHORIZED, "error.status.default");

        ResponseEntity<ErrorResponse> result = handler.handleApiException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

    @Test
    void shouldHandleApiExceptionWith403StatusCode() {
        com.credit.bladi.exception.ApiException ex = new com.credit.bladi.exception.ApiException(
            com.credit.bladi.exception.ApiStatus.FORBIDDEN, "error.status.default");

        ResponseEntity<ErrorResponse> result = handler.handleApiException(ex);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    @Test
    void shouldHandleApiExceptionWith423StatusCode() {
        com.credit.bladi.exception.ApiException ex = new com.credit.bladi.exception.ApiException(
            com.credit.bladi.exception.ApiStatus.LOCKED, "error.status.default");

        ResponseEntity<ErrorResponse> result = handler.handleApiException(ex);

        assertEquals(HttpStatus.LOCKED, result.getStatusCode());
    }
}
