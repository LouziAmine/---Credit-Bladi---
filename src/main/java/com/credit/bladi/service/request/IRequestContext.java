package com.credit.bladi.service.request;

import com.credit.bladi.model.enums.Language;

public interface IRequestContext {
    String getRequestId();

    String getClientIp();

    Language getLanguage();
}
