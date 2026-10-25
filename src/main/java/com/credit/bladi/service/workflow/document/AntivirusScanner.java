package com.credit.bladi.service.workflow.document;

public interface AntivirusScanner {
    boolean isInfected(byte[] content);
}
