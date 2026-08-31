package com.credit.bladi.exception;

public class EnterpriseUserLinkException extends ApiException {
    private final EnterpriseUserLinkExceptionType type;

    public enum EnterpriseUserLinkExceptionType {
        LINK_NOT_FOUND,
        LINK_NOT_PENDING,
        LINK_NOT_APPROVED,
        LINK_ALREADY_ACTIVE,
        REGISTRATION_NUMBER_NOT_FOUND,
        USER_NOT_CLIENT,
        ENTERPRISE_NOT_LINKED,
        DEMAND_NOT_LINKED
    }

    private EnterpriseUserLinkException(EnterpriseUserLinkExceptionType type, ApiStatus status, String messageKey) {
        super(status, messageKey);
        this.type = type;
    }

    public static EnterpriseUserLinkException of(EnterpriseUserLinkExceptionType type) {
        return switch (type) {
            case LINK_NOT_FOUND ->
                new EnterpriseUserLinkException(type, ApiStatus.NOT_FOUND, "enterpriseUserLink.notFound");
            case LINK_NOT_PENDING ->
                new EnterpriseUserLinkException(type, ApiStatus.CONFLICT, "enterpriseUserLink.notPending");
            case LINK_NOT_APPROVED ->
                new EnterpriseUserLinkException(type, ApiStatus.CONFLICT, "enterpriseUserLink.notApproved");
            case LINK_ALREADY_ACTIVE ->
                new EnterpriseUserLinkException(type, ApiStatus.CONFLICT, "enterpriseUserLink.alreadyActive");
            case REGISTRATION_NUMBER_NOT_FOUND -> new EnterpriseUserLinkException(type, ApiStatus.NOT_FOUND,
                "enterpriseUserLink.registrationNumberNotFound");
            case USER_NOT_CLIENT -> new EnterpriseUserLinkException(type, ApiStatus.UNPROCESSABLE_CONTENT,
                "enterpriseUserLink.userNotClient");
            case ENTERPRISE_NOT_LINKED ->
                new EnterpriseUserLinkException(type, ApiStatus.FORBIDDEN, "enterpriseUserLink.enterpriseNotLinked");
            case DEMAND_NOT_LINKED ->
                new EnterpriseUserLinkException(type, ApiStatus.FORBIDDEN, "enterpriseUserLink.demandNotLinked");
        };
    }

    public EnterpriseUserLinkExceptionType getType() {
        return type;
    }
}
