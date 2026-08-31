package com.credit.bladi.service.file;

public interface FileContentInspector {
    String detectContentType(byte[] content);

    String extensionFor(String contentType);

    boolean isReadableWorkbook(byte[] content);
}
