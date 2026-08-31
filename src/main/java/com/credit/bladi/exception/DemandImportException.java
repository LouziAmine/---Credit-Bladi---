package com.credit.bladi.exception;

public class DemandImportException extends ApiException {
    private final DemandImportExceptionType type;

    public enum DemandImportExceptionType {
        FILE_EMPTY,
        FILE_TOO_LARGE,
        FILE_TYPE_NOT_ALLOWED,
        JOB_EXECUTION_NOT_FOUND,
        JOB_ALREADY_RUNNING
    }

    private DemandImportException(DemandImportExceptionType type, ApiStatus status, String messageKey) {
        super(status, messageKey);
        this.type = type;
    }

    public static DemandImportException of(DemandImportExceptionType type) {
        return switch (type) {
            case FILE_EMPTY -> new DemandImportException(type, ApiStatus.UNPROCESSABLE_CONTENT,
                "demandImport.fileEmpty");
            case FILE_TOO_LARGE -> new DemandImportException(type, ApiStatus.UNPROCESSABLE_CONTENT,
                "demandImport.fileTooLarge");
            case FILE_TYPE_NOT_ALLOWED -> new DemandImportException(type, ApiStatus.UNPROCESSABLE_CONTENT,
                "demandImport.fileTypeNotAllowed");
            case JOB_EXECUTION_NOT_FOUND -> new DemandImportException(type, ApiStatus.NOT_FOUND,
                "demandImport.jobExecutionNotFound");
            case JOB_ALREADY_RUNNING -> new DemandImportException(type, ApiStatus.CONFLICT,
                "demandImport.jobAlreadyRunning");
        };
    }

    public DemandImportExceptionType getType() {
        return type;
    }
}
