package com.credit.bladi.model.workflow;

public record DownloadableDocument(DemandDocumentModel document, byte[] content) {
}
