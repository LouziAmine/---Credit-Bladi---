package com.credit.bladi.exception.handler;

import com.credit.bladi.exception.ApiException;
import com.credit.bladi.exception.ApiStatus;
import com.credit.bladi.exception.AuthException;
import com.credit.bladi.exception.BAMException;
import com.credit.bladi.exception.BamParametersException;
import com.credit.bladi.exception.CreditRateException;
import com.credit.bladi.exception.ErrorResponse;
import com.credit.bladi.exception.MourabahaParametersException;
import com.credit.bladi.exception.SimulationException;
import com.credit.bladi.exception.ValidationException;
import com.credit.bladi.exception.ValidationException.ValidationExceptionType;
import com.credit.bladi.service.request.IRequestContext;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(ApiExceptionHandler.class);

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String APP_CODE = "CB";

    private static final String ERROR_DESCRIPTION = "One or more invalid fields: %s. Please check your request.";

    private static final String ERROR_MESSAGE_BINDING = "Field '%s' is invalid or missing. Please check your request.";

    private static final String TYPE_ERROR_MESSAGE_BINDING =
        "Field '%s' has an invalid type. Please check your request.";

    private final IRequestContext requestContext;

    public ApiExceptionHandler(IRequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        ErrorResponse body = new ErrorResponse(
            requestId(), now(), errorCode(HttpStatus.BAD_REQUEST.value()), buildErrorDescription(e));
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public final ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()),
            HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public final ResponseEntity<ErrorResponse> handleHttpMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException e) {
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.NOT_ACCEPTABLE.value()),
            HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        Throwable cause = e.getCause();
        if (cause instanceof UnrecognizedPropertyException upe) {
            String message = ValidationException
                .of(ValidationExceptionType.UNKNOWN_KEY, upe.getPropertyName()).getMessage();
            return new ResponseEntity<>(
                new ErrorResponse(requestId(), now(), errorCode(400), message), HttpStatus.BAD_REQUEST);
        }
        if (cause instanceof MismatchedInputException mie) {
            return handleMismatchedInputException(mie);
        }
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MismatchedInputException.class)
    protected ResponseEntity<ErrorResponse> handleMismatchedInputException(MismatchedInputException e) {
        if ((e.getTargetType() != null && e.getTargetType().isArray())
            || (e.getMessage() != null && e.getMessage().contains("Trailing token"))) {
            String message = ValidationException.of(ValidationExceptionType.BATCH_NOT_ALLOWED).getMessage();
            return new ResponseEntity<>(
                new ErrorResponse(requestId(), now(), errorCode(400), message), HttpStatus.BAD_REQUEST);
        }
        List<JacksonException.Reference> path = e.getPath();
        String fieldInfo = path.stream()
            .skip(path.size() < 2 ? 0 : path.size() - 2)
            .map(ref -> ref.getPropertyName() != null ? ref.getPropertyName() : "[" + ref.getIndex() + "]")
            .collect(Collectors.joining("."));
        if (fieldInfo.isBlank()) {
            fieldInfo = "unknown";
        }
        return new ResponseEntity<>(new ErrorResponse(requestId(), now(), errorCode(400),
            String.format(TYPE_ERROR_MESSAGE_BINDING, fieldInfo)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApiException.class)
    public final ResponseEntity<ErrorResponse> handleApiException(ApiException e) {
        LOGGER.error("Service error [{}]", e.getStatusCode().value());
        return new ResponseEntity<>(buildErrorResponse(e.getStatusCode().value()), toHttpStatus(e.getStatusCode()));
    }

    @ExceptionHandler(AuthException.class)
    public final ResponseEntity<ErrorResponse> handleAuthException(AuthException e) {
        LOGGER.warn("Auth error [{}]: {}", e.getType(), e.getMessage());
        ErrorResponse body = new ErrorResponse(
            requestId(), now(), errorCode(e.getStatusCode().value()), e.getMessage());
        return new ResponseEntity<>(body, toHttpStatus(e.getStatusCode()));
    }

    @ExceptionHandler(BAMException.class)
    public final ResponseEntity<ErrorResponse> handleBAMException(BAMException e) {
        LOGGER.warn("BAM rule violation [{}]: {}", e.getType(), e.getMessage());
        ErrorResponse body = new ErrorResponse(
            requestId(), now(), errorCode(e.getStatusCode().value()), e.getMessage());
        return new ResponseEntity<>(body, toHttpStatus(e.getStatusCode()));
    }

    @ExceptionHandler(SimulationException.class)
    public final ResponseEntity<ErrorResponse> handleSimulationException(SimulationException e) {
        ErrorResponse body = new ErrorResponse(
            requestId(), now(), errorCode(e.getStatusCode().value()), e.getMessage());
        return new ResponseEntity<>(body, toHttpStatus(e.getStatusCode()));
    }

    @ExceptionHandler(BamParametersException.class)
    public final ResponseEntity<ErrorResponse> handleBamParametersException(BamParametersException e) {
        ErrorResponse body = new ErrorResponse(
            requestId(), now(), errorCode(e.getStatusCode().value()), e.getMessage());
        return new ResponseEntity<>(body, toHttpStatus(e.getStatusCode()));
    }

    @ExceptionHandler(CreditRateException.class)
    public final ResponseEntity<ErrorResponse> handleCreditRateException(CreditRateException e) {
        ErrorResponse body = new ErrorResponse(
            requestId(), now(), errorCode(e.getStatusCode().value()), e.getMessage());
        return new ResponseEntity<>(body, toHttpStatus(e.getStatusCode()));
    }

    @ExceptionHandler(MourabahaParametersException.class)
    public final ResponseEntity<ErrorResponse> handleMourabahaParametersException(MourabahaParametersException e) {
        ErrorResponse body = new ErrorResponse(
            requestId(), now(), errorCode(e.getStatusCode().value()), e.getMessage());
        return new ResponseEntity<>(body, toHttpStatus(e.getStatusCode()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public final ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(
            MissingServletRequestParameterException e) {
        ErrorResponse body = new ErrorResponse(requestId(), now(), errorCode(HttpStatus.BAD_REQUEST.value()),
            String.format(ERROR_MESSAGE_BINDING, e.getParameterName()));
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public final ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException e) {
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public final ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED.value()),
            HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public final ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException e) {
        ErrorResponse body = new ErrorResponse(requestId(), now(), errorCode(HttpStatus.BAD_REQUEST.value()),
            String.format(TYPE_ERROR_MESSAGE_BINDING, e.getName()));
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public final ResponseEntity<ErrorResponse> handleOptimisticLockingFailure(OptimisticLockingFailureException e) {
        LOGGER.warn("Optimistic locking conflict: {}", e.getMessage());
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.CONFLICT.value()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public final ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        LOGGER.warn("Data integrity violation: {}", e.getMessage());
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.CONFLICT.value()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException e) {
        LOGGER.warn("Access denied: {}", e.getMessage());
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
            .map(v -> v.getMessage())
            .findFirst()
            .orElse(getErrorMessage(HttpStatus.BAD_REQUEST.value()));
        return new ResponseEntity<>(
            new ErrorResponse(requestId(), now(), errorCode(HttpStatus.BAD_REQUEST.value()), message),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public final ResponseEntity<ErrorResponse> handleValidationException(ValidationException e) {
        ErrorResponse body = new ErrorResponse(
            requestId(), now(), errorCode(HttpStatus.BAD_REQUEST.value()), e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        LOGGER.warn("Invalid domain input: {}", e.getMessage());
        ErrorResponse body = new ErrorResponse(
            requestId(), now(), errorCode(HttpStatus.BAD_REQUEST.value()), e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponse> handleUnexpectedException(Exception e) {
        LOGGER.error("Unknown error : ", e);
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value()),
            HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponse buildErrorResponse(int statusCode) {
        return new ErrorResponse(requestId(), now(), errorCode(statusCode), getErrorMessage(statusCode));
    }

    private String getErrorMessage(int statusCode) {
        return switch (statusCode) {
            case 400 -> "Invalid payload or incorrect JSON format. Please check your request.";
            case 401 -> "Access to this resource requires valid authentication. Please check your credentials.";
            case 403 -> "Access denied for this request. Please check your permissions.";
            case 404 -> "The resource you requested does not exist or is not available.";
            case 405 -> "This HTTP method is not supported for this resource.";
            case 406 -> "None of the available representations for this resource matches the Accept header sent.";
            case 409 -> "This resource was modified concurrently. Please retry with the latest version.";
            case 415 -> "Unsupported Content-Type. Please use application/json.";
            default -> "Internal server error, please try again later.";
        };
    }

    private String requestId() {
        String id = requestContext.getRequestId();
        return id != null ? id : UUID.randomUUID().toString();
    }

    private String now() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }

    private String buildErrorDescription(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        if (CollectionUtils.isEmpty(fieldErrors)) {
            return getErrorMessage(400);
        }

        Set<String> invalidFields = fieldErrors.stream()
            .map(FieldError::getField)
            .collect(Collectors.toSet());

        if (invalidFields.size() == 1) {
            FieldError fieldError = fieldErrors.getFirst();
            if (fieldError.isBindingFailure()) {
                return String.format(ERROR_MESSAGE_BINDING, fieldError.getField());
            }
            return fieldError.getDefaultMessage();
        }

        return String.format(ERROR_DESCRIPTION, String.join("; ", invalidFields));
    }

    private String errorCode(int statusCode) {
        return String.format("%s-%s", APP_CODE, statusCode);
    }

    private static HttpStatus toHttpStatus(ApiStatus status) {
        return HttpStatus.valueOf(status.value());
    }
}