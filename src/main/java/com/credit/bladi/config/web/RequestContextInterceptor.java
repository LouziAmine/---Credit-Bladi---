package com.credit.bladi.config.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;


public class RequestContextInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID_HEADER = "x-request-id";
    private static final String REQUEST_ID_MDC_KEY = "requestId";

    private final RequestContext requestContext;

    public RequestContextInterceptor(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (RequestIdValidator.isValid(requestId)) {
            requestContext.setRequestId(requestId);
        }
        MDC.put(REQUEST_ID_MDC_KEY, requestContext.getRequestId());
        requestContext.setClientIp(ClientIpResolver.resolve(request));
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) {
        MDC.remove(REQUEST_ID_MDC_KEY);
    }
}
