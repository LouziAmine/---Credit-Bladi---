package com.credit.bladi.service.workflow.document;

public interface DocumentStorage {
    void store(Long demandId, String storedFilename, byte[] content);

    byte[] load(Long demandId, String storedFilename);

    void delete(Long demandId, String storedFilename);
}
