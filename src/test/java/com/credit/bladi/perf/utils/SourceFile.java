package com.credit.bladi.perf.utils;

public interface SourceFile {

    String SIMULATION_SALA_SOURCE = "gatling_files/simulation/sala/salaRequests.csv";
    String SIMULATION_FONC_SOURCE = "gatling_files/simulation/fonc/foncRequests.csv";
    String SIMULATION_RETR_SOURCE = "gatling_files/simulation/retr/retrRequests.csv";
    String SIMULATION_PROF_SOURCE = "gatling_files/simulation/prof/profRequests.csv";
    String SIMULATION_INDE_SOURCE = "gatling_files/simulation/inde/indeRequests.csv";
    String SIMULATION_MRE_SOURCE = "gatling_files/simulation/mre/mreRequests.csv";

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