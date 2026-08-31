package com.credit.bladi.model.workflow;

import java.util.Arrays;
import java.util.Objects;

public record UploadedFile(String originalFilename, byte[] content) {
    public UploadedFile {
        Objects.requireNonNull(content, "content cannot be null");
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof UploadedFile other && Objects.equals(originalFilename, other.originalFilename)
            && Arrays.equals(content, other.content);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(originalFilename) + Arrays.hashCode(content);
    }

    @Override
    public String toString() {
        return "UploadedFile[originalFilename=" + originalFilename + ", size=" + content.length + "]";
    }
}
