package com.credit.bladi.exception;

public class CreditFileException extends ApiException {
    private final CreditFileExceptionType type;

    public enum CreditFileExceptionType {
        CREDIT_FILE_NOT_FOUND,
        INSTALLMENT_NOT_FOUND,
        INSTALLMENT_NOT_FOR_CREDIT_FILE,
        INSTALLMENT_ALREADY_PAID,
        INSTALLMENT_ALREADY_DECLARED,
        INSTALLMENT_NOT_DECLARED,
        CREDIT_FILE_CLOSED,
        EARLY_REPAYMENT_ARREARS_OUTSTANDING,
        EARLY_REPAYMENT_AMOUNT_INVALID,
        EARLY_REPAYMENT_NOT_AVAILABLE_FOR_PRODUCT
    }

    private CreditFileException(CreditFileExceptionType type, ApiStatus status, String messageKey) {
        super(status, messageKey);
        this.type = type;
    }

    public static CreditFileException of(CreditFileExceptionType type) {
        return switch (type) {
            case CREDIT_FILE_NOT_FOUND ->
                new CreditFileException(type, ApiStatus.NOT_FOUND, "creditFile.notFound");
            case INSTALLMENT_NOT_FOUND ->
                new CreditFileException(type, ApiStatus.NOT_FOUND, "creditFile.installmentNotFound");
            case INSTALLMENT_NOT_FOR_CREDIT_FILE ->
                new CreditFileException(type, ApiStatus.NOT_FOUND, "creditFile.installmentNotForCreditFile");
            case INSTALLMENT_ALREADY_PAID ->
                new CreditFileException(type, ApiStatus.CONFLICT, "creditFile.installmentAlreadyPaid");
            case INSTALLMENT_ALREADY_DECLARED ->
                new CreditFileException(type, ApiStatus.CONFLICT, "creditFile.installmentAlreadyDeclared");
            case INSTALLMENT_NOT_DECLARED ->
                new CreditFileException(type, ApiStatus.CONFLICT, "creditFile.installmentNotDeclared");
            case CREDIT_FILE_CLOSED -> new CreditFileException(type, ApiStatus.CONFLICT, "creditFile.closed");
            case EARLY_REPAYMENT_ARREARS_OUTSTANDING ->
                new CreditFileException(type, ApiStatus.CONFLICT, "creditFile.earlyRepaymentArrearsOutstanding");
            case EARLY_REPAYMENT_AMOUNT_INVALID -> new CreditFileException(type, ApiStatus.UNPROCESSABLE_CONTENT,
                "creditFile.earlyRepaymentAmountInvalid");
            case EARLY_REPAYMENT_NOT_AVAILABLE_FOR_PRODUCT -> new CreditFileException(type,
                ApiStatus.UNPROCESSABLE_CONTENT, "creditFile.earlyRepaymentNotAvailableForProduct");
        };
    }

    public CreditFileExceptionType getType() {
        return type;
    }
}
