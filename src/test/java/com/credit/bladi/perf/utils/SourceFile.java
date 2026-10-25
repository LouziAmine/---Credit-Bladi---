package com.credit.bladi.perf.utils;

public interface SourceFile {
    String MORTGAGE_CREDIT_SALA_SOURCE = "gatling_files/mortgage-credit/sala/salaRequests.csv";
    String MORTGAGE_CREDIT_FONC_SOURCE = "gatling_files/mortgage-credit/fonc/foncRequests.csv";
    String MORTGAGE_CREDIT_RETR_SOURCE = "gatling_files/mortgage-credit/retr/retrRequests.csv";
    String MORTGAGE_CREDIT_PROF_SOURCE = "gatling_files/mortgage-credit/prof/profRequests.csv";
    String MORTGAGE_CREDIT_INDE_SOURCE = "gatling_files/mortgage-credit/inde/indeRequests.csv";
    String MORTGAGE_CREDIT_MRE_SOURCE = "gatling_files/mortgage-credit/mre/mreRequests.csv";

    String AUTH_LOGIN_SOURCE = "gatling_files/auth/loginRequests.csv";

    String BAM_PATCH_SOURCE = "gatling_files/bam/bamParametersPatch.csv";
    String BAM_PAGINATION_SOURCE = "gatling_files/bam/bamParametersPagination.csv";

    String CREDIT_RATE_PATCH_SOURCE = "gatling_files/creditrate/creditRatePatch.csv";
    String CREDIT_RATE_PAGINATION_SOURCE = "gatling_files/creditrate/creditRatePagination.csv";

    String MOURABAHA_PATCH_SOURCE = "gatling_files/mourabaha/mourabahaParametersPatch.csv";
    String MOURABAHA_PAGINATION_SOURCE = "gatling_files/mourabaha/mourabahaParametersPagination.csv";

    String USERS_CREATE_SOURCE = "gatling_files/user/usersCreate.csv";
    String USERS_PAGINATION_SOURCE = "gatling_files/user/usersPagination.csv";

    String AUDIT_LOG_PAGINATION_SOURCE = "gatling_files/audit/auditLogPagination.csv";
}
